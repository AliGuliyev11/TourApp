package com.mycode.tourapptelegrambot.bot.botfacade;


import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.dto.BotStateSendMessage;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;

/**
 * This is main telegram bot class
 *
 * @author Ali Guliyev
 * @version 1.0
 * @apiNote Every request enters this class
 */


@Component
@Slf4j
public class TelegramFacade {

    private final BotStateContext botStateContext;
    private final TourAppBot telegramBot;
    private final QuestionRepo questionRepo;
    private final QuestionActionRepo questionActionRepo;
    private final UserRepo userRepo;
    private final QuestionIdAndNextCache questionIdAndNextCache;
    private final CalendarCache calendarCache;
    private final ButtonAndMessageCache buttonTypeAndMessage;
    private final MessageBoolCache messageBoolCache;
    private final BotStateCache botStateCache;
    private final OrderCache orderCache;
    private final OfferCache offerCache;
    private final LocaleMessageService messageService;
    private final OfferService offerService;


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
     *
     * @param update updated message which sended by user
     * @return BotApiMethod<?>
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
            replyMessage = messageHasNotText(message);
        } else if (message != null && message.hasText()) {
            replyMessage = messageHasText(message, update);
        }
        return replyMessage;
    }


    /**
     * This method for when message not null and message hasn't text
     *
     * @param message message sended by user
     * @return SendMessage
     * @apiNote For example,message.hasVoice(),message.hasDocument etc.
     * If message type is reply and current button type is Button_Keyboard program handle this message
     * But if message type is reply and current button type isn't Button_Keyboard program delete last message
     */

    @SneakyThrows
    private SendMessage messageHasNotText(Message message) {
        SendMessage replyMessage;
        if (buttonTypeAndMessage.get(message.getFrom().getId()) != null &&
                buttonTypeAndMessage.get(message.getFrom().getId()).getQuestionType().equals(QuestionType.Button_Keyboard)
                && message.hasContact()) {
            replyMessage = handleInputMessage(message);
        } else {
            telegramBot.execute(DeleteMessage.builder().chatId(String.valueOf(message.getChatId())).messageId(message.getMessageId()).build());
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("").build();
        }
        return replyMessage;
    }

    /**
     * This method for when message not null and message has text
     *
     * @param message message sended by user
     * @param update  updated message sended by user
     * @return SendMessage
     * @apiNote If message has text and not equal bot command type and cached message type is Button program deletes last message
     * But,if message has text and equal bot command or something else program handle this message
     */
    private SendMessage messageHasText(Message message, Update update) throws TelegramApiException {
        SendMessage replyMessage;
        log.info("New message from User:{}, chatId: {},  with text: {}",
                message.getFrom().getUserName(), message.getChatId(), message.getText());

        if (!message.isCommand() && (offerService.checkUserOffer(message.getFrom().getId()) ||
                messageBoolCache.get(update.getMessage().getFrom().getId()) != null &&
                        !messageBoolCache.get(update.getMessage().getFrom().getId()).getSend()
        )) {
            telegramBot.execute(DeleteMessage.builder().chatId(String.valueOf(message.getChatId())).messageId(message.getMessageId()).build());
            return SendMessage.builder().chatId(String.valueOf(message.getChatId())).text("").build();
        }
        replyMessage = handleInputMessage(message);
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
     *
     * @param chatId sending message to user
     * @param voice  voice file which sended by user
     * @return SendMessage
     * @apiNote This API owns to IBM cloud
     * Authenticator-API key
     * setServiceUrl-API url
     * for version 1.1
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
     *
     * @param message message which sended by user
     * @return SendMessage
     */

    @SneakyThrows
    private SendMessage handleInputMessage(Message message) {
        String inputMsg = messageText(message);
        Long userId = message.getFrom().getId();
        String chatId = String.valueOf(message.getChatId());
        BotState botState = null;
        SendMessage replyMessage;
        BotStateSendMessage botStateSendMessage;
        switch (inputMsg) {
            case "/new":
            case "/start":
                botStateSendMessage = switchStartCase(userId, chatId);
                replyMessage = botStateSendMessage.getSendMessage();
                botState = botStateSendMessage.getBotState();
                break;
            case "/stop":
                replyMessage = stopCase(chatId, userId);
                break;
            case "/continue":
                botStateSendMessage = continueCase(userId, chatId, message.getMessageId());
                replyMessage = botStateSendMessage.getSendMessage();
                botState = botStateSendMessage.getBotState();
                break;
            default:
                botStateSendMessage = defaultCase(userId, chatId, message);
                replyMessage = botStateSendMessage.getSendMessage();
                botState = botStateSendMessage.getBotState();
                break;
        }

        botStateCache.save(CurrentBotState.builder().userId(userId).botState(botState).build());
        telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
        return replyMessage;
    }

    /**
     * Method for getting message text
     *
     * @param message current message
     * @return String
     */

    private String messageText(Message message) {
        String inputMsg;
        if (message.hasContact()) {
            inputMsg = message.getContact().getPhoneNumber().replaceAll("[\\s]", "");
        } else {
            inputMsg = message.getText().toLowerCase();
        }
        return inputMsg;
    }

    /**
     * When user input is /start program enters this method
     *
     * @param chatId private chat id
     * @param userId current user id
     * @return BotStateSendMessage see also dto package
     * @apiNote This method's else statement go to startCase() method
     */

    private BotStateSendMessage switchStartCase(Long userId, String chatId) {
        SendMessage replyMessage;
        BotState botState = null;
        if (orderCache.get(userId).getLanguage() != null) {
            replyMessage = SendMessage.builder().chatId(chatId)
                    .text(messageService.getMessage("start.cache", orderCache.get(userId).getLanguage())).parseMode("HTML").build();
        } else {
            replyMessage = startCase(userId, chatId);
            botState = BotState.FILLING_TOUR;
        }
        return BotStateSendMessage.builder().botState(botState).sendMessage(replyMessage).build();

    }

    /**
     * When user input is not equal other cases program enters this method
     *
     * @param userId  current user id
     * @param chatId  private chat id
     * @param message current message
     * @return BotStateSendMessage see also dto package
     */

    private BotStateSendMessage defaultCase(Long userId, String chatId, Message message) {
        SendMessage replyMessage;
        BotState botState = null;
        if (buttonTypeAndMessage.get(userId) == null && orderCache.get(userId).getLanguage() != null) {
            replyMessage = SendMessage.builder().chatId(chatId)
                    .text(messageService.getMessage("default.cache", orderCache.get(userId).getLanguage())).parseMode("HTML").build();
        } else if (orderCache.get(userId).getLanguage() == null) {
            replyMessage = SendMessage.builder().chatId(chatId)
                    .text(messageService.getMessage("stop.continue", orderCache.get(userId).getLanguage())).parseMode("HTML").build();
        } else {
            botState = botStateCache.get(userId).getBotState();
            replyMessage = botStateContext.processInputMessage(botState, message);
        }
        return BotStateSendMessage.builder().botState(botState).sendMessage(replyMessage).build();
    }

    /**
     * When user input is /continue program enters this method
     *
     * @param userId    current user id
     * @param messageId current message id
     * @param chatId    private chat id
     * @return BotStateSendMessage see also dto package
     */

    public BotStateSendMessage continueCase(Long userId, String chatId, Integer messageId) {
        SendMessage replyMessage;
        BotState botState = null;
        if (buttonTypeAndMessage.get(userId) != null) {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getPrev()).get();
            replyMessage = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    question, buttonTypeAndMessage, messageBoolCache, messageService, orderCache.get(userId).getLanguage());
            botState = BotState.FILLING_TOUR;
        } else {
            if (orderCache.get(userId).getLanguage() == null) {
                replyMessage = SendMessage.builder().chatId(chatId)
                        .text(messageService.getMessage("stop.continue", orderCache.get(userId).getLanguage())).parseMode("HTML").build();
            } else {
                replyMessage = SendMessage.builder().chatId(chatId)
                        .text(messageService.getMessage("continue.message", orderCache.get(userId).getLanguage())).parseMode("HTML").build();
            }
        }
        return BotStateSendMessage.builder().botState(botState).sendMessage(replyMessage).build();
    }

    /**
     * When user input is /stop program enters this method
     *
     * @param chatId private chat id
     * @param userId current user id
     * @return SendMessage
     */

    @SneakyThrows
    private SendMessage stopCase(String chatId, Long userId) {
        telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
        clearCache(userId);
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        return SendMessage.builder().chatId(chatId)
                .text(messageService.getMessage("stop.continue", orderCache.get(userId).getLanguage()))
                .replyMarkup(replyKeyboardRemove).parseMode("HTML").build();
    }

    /**
     * This method for clear cache
     *
     * @param userId current user id for clearing cache and de-activate current session
     * @apiNote When user input is /stop program clear cache
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

    /**
     * This method for start case
     *
     * @param userId current user id
     * @param chatId current chat id
     * @return SendMessage
     * @apiNote When user input is /start program first sends photo
     * Then asks language
     */

    @SneakyThrows
    private SendMessage startCase(Long userId, String chatId) {

        Languages languages = orderCache.get(userId).getLanguage();
        telegramBot.execute(SendChatAction.builder().action("TYPING").chatId(chatId).build());
        telegramBot.sendPhoto(chatId, messageService.getMessage("startCase.firstMessage", languages) + Emojis.Beach,
                messageService.getMessage("startCase.photoPath", languages));
        messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).build());
        String uuid = UUID.randomUUID().toString();
        orderCache.save(CurrentOrder.builder().userId(userId).order(Order.builder().userId(uuid).chatId(chatId).build()).build());
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(messageService.getMessage("startCase.askLang", languages))
                .replyMarkup(getLanguageButtons()).parseMode("HTML").build();
        userRepo.save(MyUser.builder().id(userId).uuid(uuid).chatId(chatId).build());
        return sendMessage;
    }

    /**
     * This method fir handle callback query
     *
     * @param buttonQuery this is callback query
     * @return List of BotApiMethod<?>
     * @apiNote If update @hasCallbackQuery program enters this method
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
                return questionNull(userId, chatId, userOrder);
            }

            findCallback(buttonQuery, userOrder, questionIdAndNextCache.get(userId));
            callBackAnswer = getOrderCallbackAnswer(buttonTypeAndMessage.get(userId), userId, chatId, messageId, question);

        } else if (buttonQuery.getData().equals("loadMore")) {
            callBackAnswer = offerService.loadMore(userId, chatId);
            callBackAnswer.add(DeleteMessage.builder().chatId(String.valueOf(chatId)).messageId(messageId).build());
        } else if (buttonQuery.getData().startsWith("Offer")) {
            callBackAnswer = acceptOffer(buttonQuery, userOrder);
        } else {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);
            callBackAnswer = getDateCallbackAnswer(buttonQuery, userOrder, userId, chatId, messageId, question);
        }

        orderCache.save(CurrentOrder.builder().userId(userId).order(userOrder).build());
        System.out.println(userOrder);

        return callBackAnswer;
    }

    /**
     * When question action not next program enters this method
     *
     * @param userId    current user id
     * @param chatId    private chat id
     * @param userOrder current user order
     * @return List if BotApiMethod<?>
     * @apiNote for callback query
     */

    private List<BotApiMethod<?>> questionNull(Long userId, String chatId, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        callBackAnswer.add(new SendMessage(chatId, messageService.getMessage("ending.msg",
                userOrder.getLanguage(), Emojis.SUCCESS_MARK)));
        return callBackAnswer;
    }

    /**
     * When user accepts offer program enters this method
     *
     * @param buttonQuery callback query
     * @param userOrder   current user order
     * @return List of BotApiMethod<?>
     */

    private List<BotApiMethod<?>> acceptOffer(CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        String chatId = String.valueOf(buttonQuery.getMessage().getChatId());
        Integer messageId = buttonQuery.getMessage().getMessageId();
        Long offerId = Long.valueOf(buttonQuery.getData().substring(6));
        offerService.acceptOffer(offerId);
        callBackAnswer.add(sendAnswerCallbackQuery(messageService.getMessage("accepted.message",
                userOrder.getLanguage()), buttonQuery));
        callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        return callBackAnswer;
    }


    /**
     * This method for calendar callback answer
     *
     * @param buttonQuery callback query
     * @param userOrder   current user order
     * @param userId      current user's id
     * @param chatId      private chat id
     * @param messageId   last message id
     * @param question    current question
     * @return List of BotApiMethod<?>
     * @apiNote processCallbackQuery's else statement
     */

    private List<BotApiMethod<?>> getDateCallbackAnswer(CallbackQuery buttonQuery, Order userOrder, Long userId, String chatId,
                                                        int messageId, Question question) {
        List<BotApiMethod<?>> callBackAnswer = getDateTime(buttonQuery, userOrder);
        if (callBackAnswer.contains(null)) {
            callBackAnswer = callBackAnswer.stream().filter(Objects::nonNull).collect(Collectors.toList());
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    question, buttonTypeAndMessage, messageBoolCache, messageService, userOrder.getLanguage()));
        }
        return callBackAnswer;
    }

    /**
     * This method for order callback answer
     *
     * @param currentButtonTypeAndMessage cached button type and message
     * @param userId                      current user's id
     * @param chatId                      private chat id
     * @param messageId                   last message id
     * @param question                    current question
     * @return List of BotApiMethod<?>
     * @apiNote processCallbackQuery's 2nd if statement
     */

    private List<BotApiMethod<?>> getOrderCallbackAnswer(CurrentButtonTypeAndMessage currentButtonTypeAndMessage, Long userId,
                                                         String chatId, int messageId, Question question) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();

        callBackAnswer.add(SendMessage.builder().chatId(chatId).text("<b>" + currentButtonTypeAndMessage.getMessage() + "</b>").parseMode("HTML").build());

        if (currentButtonTypeAndMessage.getQuestionType().equals(QuestionType.Button)) {
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    question, buttonTypeAndMessage, messageBoolCache, messageService, orderCache.get(userId).getLanguage()));
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
     *
     * @param messageId   last message id
     * @param userId      current user's id
     * @param chatId      private chat id
     * @param buttonQuery callback query
     * @param userOrder   current user order
     * @return List of BotApiMethod<?>
     */

    private List<BotApiMethod<?>> getLanguageCallbackAnswer(Long userId, String chatId, int messageId, CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();

        callBackAnswer.add(getLanguageType(buttonQuery, userOrder));
        callBackAnswer.add(new SendMessage(String.valueOf(chatId), userOrder.getLanguage().name()));
        callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                questionRepo.getFirstQuestionByLanguage(userOrder.getLanguage().name()), buttonTypeAndMessage, messageBoolCache
                , messageService, userOrder.getLanguage()));

        callBackAnswer.add(EditMessageReplyMarkup.builder().chatId(String.valueOf(chatId)).messageId(messageId).replyMarkup(null).build());
        return callBackAnswer;
    }

    /**
     * Program enters to this method when program send calendar to user
     *
     * @param userOrder   current user order
     * @param buttonQuery callback query
     * @return List of BotApiMethod<?>
     */

    private List<BotApiMethod<?>> getDateTime(CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        int time = calendarCache.get(buttonQuery.getFrom().getId());
        if (buttonQuery.getData().equals(">")) {
            callBackAnswer = getNextCalendar(time, buttonQuery);
        } else if (buttonQuery.getData().equals("<")) {
            callBackAnswer = getPrevCalendar(time, buttonQuery, userOrder);
        } else if (Arrays.stream(messageService.getMessage("weekdays", userOrder.getLanguage()).split("[,]"))
                .anyMatch(a -> a.equals(buttonQuery.getData())) || buttonQuery.getData().equals(IGNORE)) {
            callBackAnswer.add(sendAnswerCallbackQuery(messageService.getMessage("ignore.message", userOrder.getLanguage(), Emojis.Times), buttonQuery));
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
     *
     * @param time        current cached time for plus month
     * @param buttonQuery callback query
     * @return List of BotApiMethod<?>
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
                .replyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time),
                        messageService, orderCache.get(userId).getLanguage())).build());
        return callBackAnswer;
    }

    /**
     * Get previous month calendar
     *
     * @param time        current cached time for plus month
     * @param buttonQuery callback query
     * @param userOrder   current user order
     * @return LIst of BotApiMethod<?>
     * @apiNote If previous month @isBefore @LocaleDate.now() program send user callback answer
     */

    private List<BotApiMethod<?>> getPrevCalendar(int time, CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        final Long userId = buttonQuery.getFrom().getId();
        final String chatId = String.valueOf(buttonQuery.getMessage().getChatId());
        final int messageId = buttonQuery.getMessage().getMessageId();
        if (time == 0) {
            callBackAnswer.add(sendAnswerCallbackQuery(messageService.getMessage("prev.calendar", userOrder.getLanguage(), Emojis.Times), buttonQuery));
        } else {
            time--;
            calendarCache.save(CalendarTime.builder().userId(userId).time(time).build());

            callBackAnswer.add(SendMessage.builder().chatId(chatId).text("Calendar" + Emojis.Clock)
                    .replyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time), messageService, userOrder.getLanguage())).build());

            callBackAnswer.add(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        }
        return callBackAnswer;
    }


    /**
     * Dynamically finding button callback data
     *
     * @param userOrder         current user order
     * @param buttonQuery       callback query
     * @param questionIdAndNext cached question id and next question id
     * @return BotApiMethod<?>
     * @apiNote Program checks if keyword which comes from database matches button's  callback data
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
     *
     * @param buttonQuery callback query
     * @param field       Order entity's current field
     * @param userId      current user id
     * @param type        current question type @see QuestionType enum
     * @param userOrder   current user order
     * @return BotApiMethod<?>
     */

    @SneakyThrows
    private BotApiMethod<?> setCalendarField(CallbackQuery buttonQuery, Field field, Long userId, QuestionType type, Order userOrder) {
        Object text = buttonQuery.getData();
        LocalDate localDate = getLocaleDate(text.toString());
        if (localDate.isBefore(LocalDate.now())) {
            return sendAnswerCallbackQuery(messageService.getMessage("prev.calendar", userOrder.getLanguage(), Emojis.Times), buttonQuery);
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
     *
     * @param questionAction current question action
     * @param userOrder      current user order
     * @param type           Order entity's current field
     * @param userId         current user id
     * @param field          current Order entity's field
     * @param questionType   current question type @see QuestionType enum
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
     *
     * @param text text for convert to LocalDate
     * @return @org.joda.time LocalDate
     */

    private LocalDate getLocaleDate(String text) {
        DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = FORMATTER.parseDateTime(text);
        return dateTime.toLocalDate();
    }


    /**
     * When user choose language button program send callback answer to user just for info
     *
     * @param buttonQuery callback query
     * @param userOrder   setting language type to new Order
     * @return BotApiMethod<?>
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

        return sendAnswerCallbackQuery(messageService.getMessage("bot.lang", userOrder.getLanguage()), buttonQuery);
    }


    /**
     * This method send user callback answer with/without alert
     *
     * @param text          text of AnswerCallbackQuery
     * @param callbackQuery callback query
     * @return AnswerCallbackQuery
     */

    private synchronized AnswerCallbackQuery sendAnswerCallbackQuery(String text, CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


}