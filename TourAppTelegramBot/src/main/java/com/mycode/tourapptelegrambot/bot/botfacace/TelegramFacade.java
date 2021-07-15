package com.mycode.tourapptelegrambot.bot.botfacace;


import com.google.common.annotations.VisibleForTesting;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.redis.RedisCache.*;
import com.mycode.tourapptelegrambot.redis.redisEntity.*;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.inlineButtons.UniversalInlineButtons;
import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import com.mycode.tourapptelegrambot.repositories.QuestionActionRepo;
import com.mycode.tourapptelegrambot.repositories.QuestionRepo;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import com.mycode.tourapptelegrambot.services.OfferService;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;
import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.*;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.WD;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class TelegramFacade {

    private BotStateContext botStateContext;
    private TourAppBot telegramBot;
    private QuestionRepo questionRepo;
    private QuestionActionRepo questionActionRepo;
    private UserRepo userRepo;
    private QuestionIdAndNextCache questionIdAndNextCache;
    private CalendarCache calendarCache;
    private ButtonAndMessageCache buttonTypeAndMessage;
    private MessageBoolCache messageBoolCache;
    private BotStateCache botStateCache;
    private OrderCache orderCache;
    private OfferCache offerCache;
    private LocaleMessageService messageService;
    private OfferService offerService;


    public TelegramFacade(@Lazy TourAppBot telegramBot, BotStateContext botStateContext,
                          QuestionRepo question, QuestionActionRepo questionActionRepo, QuestionIdAndNextCache questionIdAndNextCache,
                          CalendarCache calendarCache, ButtonAndMessageCache buttonTypeAndMessage, MessageBoolCache messageBoolCache,
                          BotStateCache botStateCache, OrderCache orderCache, LocaleMessageService service, UserRepo userRepo,
                          OfferCache offer, OfferService offerService) {
        this.telegramBot = telegramBot;
        this.botStateContext = botStateContext;
        this.questionRepo = question;
        this.questionActionRepo = questionActionRepo;
        this.questionIdAndNextCache = questionIdAndNextCache;
        this.calendarCache = calendarCache;
        this.buttonTypeAndMessage = buttonTypeAndMessage;
        this.messageBoolCache = messageBoolCache;
        this.botStateCache = botStateCache;
        this.orderCache = orderCache;
        this.messageService = service;
        this.userRepo = userRepo;
        this.offerCache = offer;
        this.offerService = offerService;
    }


    /**
     * Every time user send message program enters to this method
     */

    @SneakyThrows
    public BotApiMethod<?> handleUpdate(Update update) {

        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            for (var item : processCallbackQuery(callbackQuery)) {
                telegramBot.Execute(item);
            }
        }
        Message message = update.getMessage();

        if (message != null && !message.hasText()) {

            telegramBot.execute(DeleteMessage.builder().chatId(String.valueOf(message.getChatId())).messageId(message.getMessageId()).build());
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("").build();
        }

        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            if (!message.getText().equals("/stop") && !message.getText().equals("/continue") && !message.getText().equals("/start") &&
                    (messageBoolCache.get(update.getMessage().getFrom().getId()) == null || messageBoolCache.get(update.getMessage().getFrom().getId()) != null &&
                            !messageBoolCache.get(update.getMessage().getFrom().getId()).getSend()
                    )) {
                telegramBot.execute(DeleteMessage.builder().chatId(String.valueOf(message.getChatId())).messageId(message.getMessageId()).build());
                return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("").build();
            }
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }


    @Value("${ibm.apiKey}")
    String apiKey;
    @Value("${ibm.serviceUrl}")
    String serviceUrl;
    @Value("${voice.path}")
    String voiceFilePath;
    @Value("${voice.error}")
    String errorMessage;

    /**
     * Method for change @Voice to text
     * This API owns to IBM cloud
     * Authenticator-API key
     * setServiceUrl-API url
     * For Version 2
     */

    @SneakyThrows
    private SendMessage speechToText(Voice voice, Long chatId) {
        Authenticator authenticator = new IamAuthenticator(apiKey);
        SpeechToText speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl(serviceUrl);

        telegramBot.voice(voice);

        RecognizeOptions recognizeOptions = new RecognizeOptions.Builder()
                .audio(new FileInputStream(voiceFilePath))
                .contentType(voice.getMimeType())
                .build();

        SpeechRecognitionResults speechRecognitionResults =
                speechToText.recognize(recognizeOptions).execute().getResult();
        if (speechRecognitionResults.getResults().isEmpty()) {
            return SendMessage.builder().chatId(String.valueOf(chatId)).text(errorMessage).build();
        } else {
            List<SpeechRecognitionResult> resultsList = speechRecognitionResults.getResults().stream().filter(SpeechRecognitionResult::isXFinal).collect(Collectors.toList());
            String a = resultsList.get(0).getAlternatives().get(0).getTranscript();
            return SendMessage.builder().chatId(String.valueOf(chatId)).text(a).build();
        }
    }


    /**
     * If message has text program enters to this method
     */

    @SneakyThrows
    private SendMessage handleInputMessage(Message message) {

        String inputMsg = message.getText().toLowerCase();
        Long userId = message.getFrom().getId();
        String chatId = String.valueOf(message.getChatId());
        BotState botState = null;
        SendMessage replyMessage;
        switch (inputMsg) {
            case "/new":
            case "/start":
                if (orderCache.get(userId).getLanguage() != null) {
                    replyMessage = SendMessage.builder().chatId(chatId).text(getStartCacheMessage(orderCache.get(userId))).parseMode("HTML").build();
                } else {
                    replyMessage = startCase(userId, chatId);
                    botState = BotState.FILLING_TOUR;
                }
                break;
            case "/stop":
                telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
                clearCache(userId);
                replyMessage = SendMessage.builder().chatId(chatId).text(getStopContinueCacheMessage()).parseMode("HTML").build();
                break;
            case "/continue":
                if (buttonTypeAndMessage.get(userId) != null) {
                    Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getPrev()).get();
                    replyMessage = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, message.getMessageId(), questionIdAndNextCache,
                            question, buttonTypeAndMessage, messageBoolCache);
                    botState = BotState.FILLING_TOUR;
                } else {
                    if (orderCache.get(userId).getLanguage() == null) {
                        replyMessage = SendMessage.builder().chatId(chatId).text(getStopContinueCacheMessage()).parseMode("HTML").build();
                    } else {
                        replyMessage = SendMessage.builder().chatId(chatId).text(getContinueMessage(orderCache.get(userId))).parseMode("HTML").build();
                    }
                }

                break;
            default:
                if (buttonTypeAndMessage.get(userId) == null) {
                    replyMessage = SendMessage.builder().chatId(chatId).text(getDefaultCacheMessage(orderCache.get(userId))).parseMode("HTML").build();
                } else {
                    botState = botStateCache.get(userId).getBotState();
                    replyMessage = botStateContext.processInputMessage(botState, message);
                }
                break;
        }

        botStateCache.save(CurrentBotState.builder().userId(userId).botState(botState).build());
        telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
        return replyMessage;
    }

    /**
     * When user input is /stop program clear cache
     */

    public void clearCache(Long userId) {
        botStateCache.delete(userId);
        buttonTypeAndMessage.delete(userId);
        messageBoolCache.delete(userId);
        questionIdAndNextCache.delete(userId);
        offerCache.delete(userId);
        calendarCache.delete(userId);
        MyUser myUser = userRepo.findById(userId).get();
        String uuid = null;
        if (myUser != null) {
            uuid = myUser.getUuid();
            myUser.setUuid(UUID.randomUUID().toString());
            userRepo.save(myUser);
        }
        orderCache.delete(userId);
        offerService.clearUserOffer(userId, uuid);
    }

    @Value("${startCase.photoPath}")
    String photoPath;

    /**
     * When user input is /start program first sends photo
     * Then asks language
     */

    @SneakyThrows
    private SendMessage startCase(Long userId, String chatId) {
        telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
        telegramBot.sendPhoto(chatId, messageService.getMessage("startCase.firstMessage") + Emojis.Beach, photoPath);
        messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).build());
        String uuid = UUID.randomUUID().toString();
        orderCache.save(CurrentOrder.builder().userId(userId).order(Order.builder().userId(uuid).chatId(chatId).build()).build());
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(messageService.getMessage("startCase.askLang"))
                .replyMarkup(getLanguageButtons()).parseMode("HTML").build();
        userRepo.save(MyUser.builder().id(userId).uuid(uuid).chatId(chatId).build());
        return sendMessage;
    }

    /**
     * If update @hasCallbackQuery program enters this method
     * And when user click inline button program checks callback query's data
     * If data start with 'Lang' enters 1st if
     * Else if start with 'Order' enters 2nd if
     * Else statement for calendar
     */

    private List<BotApiMethod<?>> processCallbackQuery(CallbackQuery buttonQuery) {
        final String chatId = String.valueOf(buttonQuery.getMessage().getChatId());
        final Long userId = buttonQuery.getFrom().getId();
        final int messageId = buttonQuery.getMessage().getMessageId();
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        Order userOrder = orderCache.get(userId);

        if (buttonQuery.getData().startsWith("Lang")) {
            callBackAnswer = getLanguageCallbackAnswer(userId, chatId, messageId, buttonQuery, userOrder);
        } else if (buttonQuery.getData().startsWith("Order")) {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);

            if (question == null) {
                botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
                callBackAnswer.add(new SendMessage(chatId, sendEndingMessage(userOrder)));
                return callBackAnswer;
            }

            findCallback(buttonQuery, userOrder, questionIdAndNextCache.get(userId));
            callBackAnswer = getOrderCallbackAnswer(buttonTypeAndMessage.get(userId), userId, chatId, messageId, question);

        } else if (buttonQuery.getData().equals("loadMore")) {
            callBackAnswer = offerService.loadMore(userId, chatId);
            callBackAnswer.add(DeleteMessage.builder().chatId(String.valueOf(chatId)).messageId(messageId).build());
        } else if (buttonQuery.getData().startsWith("Offer")) {
            callBackAnswer = acceptOffer(buttonQuery,userOrder);
        } else {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);
            callBackAnswer = getDateCallbackAnswer(buttonQuery, userOrder, userId, chatId, messageId, question);
        }

        orderCache.save(CurrentOrder.builder().userId(userId).order(userOrder).build());
        System.out.println(userOrder);

        return callBackAnswer;
    }

    private List<BotApiMethod<?>> acceptOffer(CallbackQuery buttonQuery,Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        String chatId= String.valueOf(buttonQuery.getMessage().getChatId());
        Integer messageId=buttonQuery.getMessage().getMessageId();
        Long offerId = Long.valueOf(buttonQuery.getData().substring(6));
        offerService.acceptOffer(offerId);
        callBackAnswer.add(sendAnswerCallbackQuery(getAcceptedMessage(userOrder), buttonQuery));
        callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        return callBackAnswer;
    }


    /**
     * This method for calendar callback answer
     * processCallbackQuery's else statement
     */

    private List<BotApiMethod<?>> getDateCallbackAnswer(CallbackQuery buttonQuery, Order userOrder, Long userId, String chatId,
                                                        int messageId, Question question) {
        List<BotApiMethod<?>> callBackAnswer = getDateTime(buttonQuery, userOrder);
        if (callBackAnswer.contains(null)) {
            callBackAnswer = callBackAnswer.stream().filter(Objects::nonNull).collect(Collectors.toList());
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    question, buttonTypeAndMessage, messageBoolCache));
        }
        return callBackAnswer;
    }

    /**
     * This method for order callback answer
     * processCallbackQuery's 2nd if statement
     */

    private List<BotApiMethod<?>> getOrderCallbackAnswer(CurrentButtonTypeAndMessage currentButtonTypeAndMessage, Long userId,
                                                         String chatId, int messageId, Question question) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();

        callBackAnswer.add(SendMessage.builder().chatId(chatId).text("<b>" + currentButtonTypeAndMessage.getMessage() + "</b>").parseMode("HTML").build());

        if (currentButtonTypeAndMessage.getQuestionType().equals(QuestionType.Button)) {
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    question, buttonTypeAndMessage, messageBoolCache));
            callBackAnswer.add(EditMessageReplyMarkup.builder().chatId(chatId).messageId(messageId).replyMarkup(null).build());

        } else {

            callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
            botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        }
        return callBackAnswer;
    }

    /**
     * This method for language callback answer
     * processCallbackQuery's 1st if  statement
     */

    private List<BotApiMethod<?>> getLanguageCallbackAnswer(Long userId, String chatId, int messageId, CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();

        callBackAnswer.add(getLanguageType(buttonQuery, userOrder));
        callBackAnswer.add(new SendMessage(String.valueOf(chatId), userOrder.getLanguage().name()));
        callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                questionRepo.getFirstQuestionByLanguage(userOrder.getLanguage().name()), buttonTypeAndMessage, messageBoolCache));

        callBackAnswer.add(EditMessageReplyMarkup.builder().chatId(String.valueOf(chatId)).messageId(messageId).replyMarkup(null).build());
        return callBackAnswer;
    }

    /**
     * Program enters to this method when program send calendar to user
     */

    private List<BotApiMethod<?>> getDateTime(CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        int time = calendarCache.get(buttonQuery.getFrom().getId());
        if (buttonQuery.getData().equals(">")) {
            callBackAnswer = getNextCalendar(time, buttonQuery);
        } else if (buttonQuery.getData().equals("<")) {
            callBackAnswer = getPrevCalendar(time, buttonQuery, userOrder);
        } else if (Arrays.stream(WD).anyMatch(a -> a.equals(buttonQuery.getData())) || buttonQuery.getData().equals(IGNORE)) {
            callBackAnswer.add(sendAnswerCallbackQuery(sendIgnoreMessage(userOrder), buttonQuery));
        } else {
            BotApiMethod<?> answer = findCallback(buttonQuery, userOrder, questionIdAndNextCache.get(buttonQuery.getFrom().getId()));
            if (answer != null) {
                callBackAnswer.add(answer);
            } else {

                callBackAnswer.add(DeleteMessage.builder().chatId(String.valueOf(buttonQuery.getMessage().getChatId()))
                        .messageId(buttonQuery.getMessage().getMessageId()).build());
                callBackAnswer.add(SendMessage.builder().chatId(String.valueOf(buttonQuery.getMessage().getChatId()))
                        .text("<b>" + buttonTypeAndMessage.get(buttonQuery.getFrom().getId()).getMessage() + "</b>").parseMode("HTML").build());
                callBackAnswer.add(null);
            }
            calendarCache.delete(buttonQuery.getFrom().getId());

        }

        return callBackAnswer;
    }

    /**
     * Get next month calendar
     */

    private List<BotApiMethod<?>> getNextCalendar(int time, CallbackQuery buttonQuery) {
        final Long userId = buttonQuery.getFrom().getId();
        final String chatId = String.valueOf(buttonQuery.getMessage().getChatId());
        final int messageId = buttonQuery.getMessage().getMessageId();
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        time++;
        calendarCache.save(CalendarTime.builder().userId(userId).time(time).build());
        callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        callBackAnswer.add(SendMessage.builder().chatId(chatId).text("Calendar" + Emojis.Clock)
                .replyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))).build());
        return callBackAnswer;
    }

    /**
     * Get previous month calendar
     * If previous month @isBefore @LocaleDate.now() program send user callback answer
     */

    private List<BotApiMethod<?>> getPrevCalendar(int time, CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        final Long userId = buttonQuery.getFrom().getId();
        final String chatId = String.valueOf(buttonQuery.getMessage().getChatId());
        final int messageId = buttonQuery.getMessage().getMessageId();
        if (time == 0) {
            callBackAnswer.add(sendAnswerCallbackQuery(getPrevCalendarMessage(userOrder), buttonQuery));
        } else {
            time--;
            calendarCache.save(CalendarTime.builder().userId(userId).time(time).build());

            callBackAnswer.add(SendMessage.builder().chatId(chatId).text("Calendar" + Emojis.Clock)
                    .replyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))).build());

            callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        }
        return callBackAnswer;
    }


    /**
     * Dynamically finding button callback data
     * Program checks if keyword which comes from database matches button's  callback data
     * If matched with keyword, program will do some operations
     */

    @SneakyThrows
    private BotApiMethod<?> findCallback(CallbackQuery buttonQuery, Order userOrder, QuestionIdAndNext questionIdAndNext) {
        final Long userId = buttonQuery.getFrom().getId();
        List<QuestionAction> questionActions = questionActionRepo.findQuestionActionsByNext(questionIdAndNext.getNext());
        Class<?> order = userOrder.getClass();
        for (var item : questionActions) {
            Field field = order.getDeclaredField(item.getKeyword());
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (buttonQuery.getData().equals(item.getKeyword() + item.getId())) {

                if (item.getType().equals(QuestionType.Button)) {
                    QuestionAction questionAction = questionActionRepo.findById(item.getId()).get();
                    setButtonTypeDataToOrder(questionAction, type, field, userOrder, userId, item.getType());
                } else {
                    buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(item.getType())
                            .message(buttonQuery.getMessage().getText()).build());
                }
                messageBoolCache.save(MessageAndBoolean.builder().userId(buttonQuery.getFrom().getId()).send(true).build());

                break;
            } else if (item.getType().equals(QuestionType.Button_Calendar)) {
                return setCalendarField(buttonQuery, field, userId, item.getType(), userOrder);
            }
        }
        return null;
    }

    /**
     * Method for set calendar LocalDate field
     */

    @SneakyThrows
    private BotApiMethod<?> setCalendarField(CallbackQuery buttonQuery, Field field, Long userId, QuestionType type, Order userOrder) {
        Object text = buttonQuery.getData();
        LocalDate localDate = getLocaleDate(text.toString());
        if (localDate.isBefore(LocalDate.now())) {
            return sendAnswerCallbackQuery(getPrevCalendarMessage(userOrder), buttonQuery);
        }
        Date date = localDate.toDate();
        field.set(userOrder, date);
        buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(type)
                .message(text.toString()).build());
        messageBoolCache.save(MessageAndBoolean.builder().userId(buttonQuery.getFrom().getId()).send(true).build());
        return null;
    }


    /**
     * Mapping button data to order
     */

    @SneakyThrows
    private void setButtonTypeDataToOrder(QuestionAction questionAction, Class<?> type, Field field,
                                          Order userOrder, Long userId, QuestionType questionType) {
        Object text = questionAction.getText();

        if (isPrimitive(type)) {
            Object boxed = boxPrimitiveClass(type, text.toString());
            field.set(userOrder, boxed);
        } else {
            field.set(userOrder, text);
        }
        buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                .message(text.toString()).build());
    }


    /**
     * Parse string to @org.joda.time LocalDate
     */

    private LocalDate getLocaleDate(String text) {
        DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = FORMATTER.parseDateTime(text);
        return dateTime.toLocalDate();
    }


    /**
     * When user choose language button program send callback answer to user just for info
     */

    private BotApiMethod<?> getLanguageType(CallbackQuery buttonQuery, Order userOrder) {

        final Long userId = buttonQuery.getFrom().getId();

        switch (buttonQuery.getData()) {
            case "LangButtonAz":
                userOrder.setLanguage(Languages.AZ);
                break;
            case "LangButtonRu":
                userOrder.setLanguage(Languages.RU);
                break;
            default:
                userOrder.setLanguage(Languages.EN);
                break;
        }

        botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(QuestionType.Button)
                .message(userOrder.getLanguage().name()).build());
        messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(true).build());

        return sendAnswerCallbackQuery(getBotLangMessage(userOrder), buttonQuery);
    }


    /**
     * This method send user callback answer with/without alert
     */

    private synchronized AnswerCallbackQuery sendAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


}