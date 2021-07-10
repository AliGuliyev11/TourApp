package com.mycode.tourapptelegrambot.bot.botfacace;


import com.mycode.tourapptelegrambot.bot.TourAppBot;
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
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;
import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.*;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.WD;

@Component
@Slf4j
public class TelegramFacade {

    private final BotStateContext botStateContext;
    private final TourAppBot telegramBot;
    private final QuestionRepo questionRepo;
    private final QuestionActionRepo questionActionRepo;
    private final QuestionIdAndNextCache questionIdAndNextCache;
    private final CalendarCache calendarCache;
    private final ButtonAndMessageCache buttonTypeAndMessage;
    private final MessageBoolCache messageBoolCache;
    private final BotStateCache botStateCache;
    private final OrderCache orderCache;


    public TelegramFacade(@Lazy TourAppBot telegramBot, BotStateContext botStateContext,
                          QuestionRepo question, QuestionActionRepo questionActionRepo, QuestionIdAndNextCache questionIdAndNextCache,
                          CalendarCache calendarCache, ButtonAndMessageCache buttonTypeAndMessage, MessageBoolCache messageBoolCache,
                          BotStateCache botStateCache, OrderCache orderCache) {
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
    }

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
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            if (!message.getText().equals("/continue") && !message.getText().equals("/start") && messageBoolCache.get(update.getMessage().getFrom().getId()) != null &&
                    !messageBoolCache.get(update.getMessage().getFrom().getId()).getSend()) {
                telegramBot.execute(new DeleteMessage().setChatId(message.getChatId()).setMessageId(message.getMessageId()));
                return new SendMessage().setChatId(message.getChatId()).setText("");
            }
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    @SneakyThrows
    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        BotState botState = null;
        SendMessage replyMessage = null;
        switch (inputMsg) {
            case "/new":
            case "/start":
                System.out.println(orderCache.get(userId));
//                orderCache.delete(userId);
                if (orderCache.get(userId).getLanguage() != null) {
                    replyMessage = new SendMessage(chatId, "Yenidən başlamaq üçün ilk öncə <b> stop </b> yazmalısan\n /stop -prosesi bitirmək üçün \n /continue-sullarınıza davam etmək üçün").setParseMode("HTML");
                }else {
                    replyMessage = startCase(userId, chatId);
                    botState = BotState.FILLING_TOUR;
                }
                break;
            case "/stop":
                telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
                clearCache(userId);
//                botState = BotState.FILLING_PROFILE;
                break;
            case "/continue":
                if (buttonTypeAndMessage.get(userId)!= null) {
                    Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getPrev()).get();
                    replyMessage = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, message.getMessageId(), questionIdAndNextCache,
                            question, buttonTypeAndMessage, messageBoolCache);
                    botState = BotState.FILLING_TOUR;
                }else{
                    replyMessage = new SendMessage(chatId, "Sizə veriləcək sual yoxdur").setParseMode("HTML");
                }

                break;
            default:
                if (buttonTypeAndMessage.get(userId)==null) {
                    replyMessage = new SendMessage(chatId, "Yenidən başlamaq üçün ilk öncə <b> /start </b> yazmalısan\n /new -prosesi yenidən başlamaq üçün \n /stop -prosesi bitirmək üçün").setParseMode("HTML");
                } else {
                    botState = botStateCache.get(userId).getBotState();
                    replyMessage = botStateContext.processInputMessage(botState, message);
                }
                break;
        }

        botStateCache.save(CurrentBotState.builder().userId(userId).botState(botState).build());
        telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
        return replyMessage;
    }

    public void clearCache(int userId) {
        orderCache.delete(userId);
        botStateCache.delete(userId);
        buttonTypeAndMessage.delete(userId);
        messageBoolCache.delete(userId);
        questionIdAndNextCache.delete(userId);
        calendarCache.delete(userId);
    }

    @SneakyThrows
    private SendMessage startCase(int userId, Long chatId) {
        telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
        telegramBot.sendPhoto(chatId, "Əvvəlcədən xoş istirahətlər" + Emojis.Beach, "src/main/resources/static/images/tourApp.jpg");
        messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).build());
        orderCache.save(CurrentOrder.builder().userId(userId).order(Order.builder().userId(userId).chatId(chatId).build()).build());
        SendMessage sendMessage = new SendMessage(chatId, "Dil seçimini edin zəhmət olmasa:").setReplyMarkup(getLanguageButtons());
        sendMessage.setParseMode("HTML");
        return sendMessage;
    }

    private List<BotApiMethod<?>> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        final int messageId = buttonQuery.getMessage().getMessageId();
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        Order userOrder = orderCache.get(userId);


        if (buttonQuery.getData().startsWith("Lang")) {

            callBackAnswer.add(getLanguageType(buttonQuery, userOrder));
            callBackAnswer.add(new SendMessage(chatId, userOrder.getLanguage().name()));
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                    questionRepo.getFirstQuestionByLanguage(userOrder.getLanguage().name()), buttonTypeAndMessage, messageBoolCache));
            callBackAnswer.add(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId).setReplyMarkup(null));

        } else if (buttonQuery.getData().startsWith("Order")) {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);

            if (question == null) {
                botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
                callBackAnswer.add(new SendMessage(chatId, sendEndingMessage(userOrder)));
                return callBackAnswer;
            }

            findCallback(buttonQuery, userOrder, questionIdAndNextCache.get(userId));
            CurrentButtonTypeAndMessage currentButtonTypeAndMessage = buttonTypeAndMessage.get(userId);
            System.out.println(currentButtonTypeAndMessage.getQuestionType().name());

            callBackAnswer.add(new SendMessage(chatId, "<b>" + currentButtonTypeAndMessage.getMessage() + "</b>").setParseMode("HTML"));

            if (currentButtonTypeAndMessage.getQuestionType().equals(QuestionType.Button)) {
                callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                        question, buttonTypeAndMessage, messageBoolCache));
                callBackAnswer.add(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId).setReplyMarkup(null));

            } else {

                callBackAnswer.add(new DeleteMessage().setChatId(chatId).setMessageId(messageId));
                botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
            }
        } else {
            Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);
            callBackAnswer = getDateTime(buttonQuery, userOrder);
            if (callBackAnswer.contains(null)) {
                callBackAnswer = callBackAnswer.stream().filter(Objects::nonNull).collect(Collectors.toList());
                callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId, questionIdAndNextCache,
                        question, buttonTypeAndMessage, messageBoolCache));
            }

        }

        orderCache.save(CurrentOrder.builder().userId(userId).order(userOrder).build());
        System.out.println(userOrder);

        return callBackAnswer;


    }

    private List<BotApiMethod<?>> getDateTime(CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        int time = calendarCache.get(buttonQuery.getFrom().getId());
        if (buttonQuery.getData().equals(">")) {
            time++;
            calendarCache.save(CalendarTime.builder().userId(buttonQuery.getFrom().getId()).time(time).build());
            callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
            callBackAnswer.add(new SendMessage().setChatId(buttonQuery.getMessage().getChatId()).setText("Calendar" + Emojis.Clock).
                    setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))));
        } else if (buttonQuery.getData().equals("<")) {
            if (time == 0) {
                callBackAnswer.add(sendAnswerCallbackQuery(getPrevCalendarMessage(userOrder), buttonQuery));
            } else {
                time--;
                calendarCache.save(CalendarTime.builder().userId(buttonQuery.getFrom().getId()).time(time).build());
                callBackAnswer.add(new SendMessage().setChatId(buttonQuery.getMessage().getChatId()).setText("Calendar" + Emojis.Clock).
                        setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))));
                callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
            }

        } else if (Arrays.stream(WD).anyMatch(a -> a.equals(buttonQuery.getData())) || buttonQuery.getData().equals(IGNORE)) {
            callBackAnswer.add(sendAnswerCallbackQuery(sendIgnoreMessage(userOrder), buttonQuery));
        } else {
            BotApiMethod<?> answer = findCallback(buttonQuery, userOrder, questionIdAndNextCache.get(buttonQuery.getFrom().getId()));
            if (answer != null) {
                callBackAnswer.add(answer);
            } else {
                callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
                callBackAnswer.add(new SendMessage(buttonQuery.getMessage().getChatId(),
                        "<b>" + buttonTypeAndMessage.get(buttonQuery.getFrom().getId()).getMessage() + "</b>")
                        .setParseMode("HTML"));
                callBackAnswer.add(null);
            }
            calendarCache.delete(buttonQuery.getFrom().getId());

        }

        return callBackAnswer;
    }


    @SneakyThrows
    private BotApiMethod<?> findCallback(CallbackQuery buttonQuery, Order userOrder, QuestionIdAndNext questionIdAndNext) {
//        SendMessage sendMessage = messageBoolCache.get(buttonQuery.getFrom().getId()).getSendMessage();
        BotApiMethod<?> callbackAnswer = null;
        final int userId = buttonQuery.getFrom().getId();
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
                Object text = buttonQuery.getData();
                LocalDate localDate = getLocaleDate(text.toString());
                if (localDate.isBefore(LocalDate.now())) {
                    return sendAnswerCallbackQuery(getPrevCalendarMessage(userOrder), buttonQuery);
                }
                field.set(userOrder, localDate);
                buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(item.getType())
                        .message(text.toString()).build());
                messageBoolCache.save(MessageAndBoolean.builder().userId(buttonQuery.getFrom().getId()).send(true).build());
            }
        }
        return callbackAnswer;
    }

    @SneakyThrows
    private void setButtonTypeDataToOrder(QuestionAction questionAction, Class<?> type, Field field,
                                          Order userOrder, int userId, QuestionType questionType) {
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


    private LocalDate getLocaleDate(String text) {
        DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = FORMATTER.parseDateTime(text.toString());
        LocalDate localDate = dateTime.toLocalDate();
        return localDate;
    }

    private BotApiMethod<?> getLanguageType(CallbackQuery buttonQuery, Order userOrder) {

        final int userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer;

        switch (buttonQuery.getData()) {
            case "LangButtonAz":
                userOrder.setLanguage(Languages.AZ);
                callBackAnswer = sendAnswerCallbackQuery("Botun dili Azərbaycan dili olaraq təyin olundu", buttonQuery);

                break;
            case "LangButtonRu":
                userOrder.setLanguage(Languages.RU);
                callBackAnswer = sendAnswerCallbackQuery("Язык Ботуна был определен как русский", buttonQuery);
                break;
            default:
                userOrder.setLanguage(Languages.EN);
                callBackAnswer = sendAnswerCallbackQuery("Bot's language was designated as English", buttonQuery);
                break;
        }

        botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        buttonTypeAndMessage.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(QuestionType.Button)
                .message(userOrder.getLanguage().name()).build());
        messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(true).build());

        return callBackAnswer;
    }


    private synchronized AnswerCallbackQuery sendAnswerCallbackQuery(String text, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


}
