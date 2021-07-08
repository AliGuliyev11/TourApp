package com.mycode.tourapptelegrambot.bot.botfacace;


import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.cache.UserOrderCache;
import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
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
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.mycode.tourapptelegrambot.bot.botfacace.BotStateContext.isButtonType;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;
import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.*;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.WD;

@Component
@Slf4j
public class TelegramFacade {

    private BotStateContext botStateContext;
    private UserOrderCache userOrderCache;
    //    private MainMenuService mainMenuService;
    private TourAppBot telegramBot;
//    private ReplyMessagesService messagesService;
//    private FillingProfileHandler fillingProfileHandler;

    QuestionRepo questionRepo;
    QuestionActionRepo questionActionRepo;
    ModelMapper modelMapper = new ModelMapper();

//    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
//                          @Lazy StudentBot telegramBot, ReplyMessagesService messagesService, FillingProfileHandler fillingProfileHandler) {
//
//        this.botStateContext = botStateContext;
//        this.userDataCache = userDataCache;
//        this.mainMenuService = mainMenuService;
//        this.telegramBot = telegramBot;
//        this.messagesService = messagesService;
//        this.fillingProfileHandler = fillingProfileHandler;
//    }


    public TelegramFacade(@Lazy TourAppBot telegramBot, UserOrderCache userOrderCache, BotStateContext botStateContext,
                          QuestionRepo question, QuestionActionRepo questionActionRepo) {
        this.telegramBot = telegramBot;
        this.userOrderCache = userOrderCache;
        this.botStateContext = botStateContext;
        this.questionRepo = question;
        this.questionActionRepo = questionActionRepo;
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
            case "/start":
                telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
                telegramBot.sendPhoto(chatId, "Əvvəlcədən xoş istirahətlər" + Emojis.Beach, "src/main/resources/static/images/tourApp.jpg");
                replyMessage = new SendMessage(chatId, "Dil seçimini edin zəhmət olmasa:").setReplyMarkup(getLanguageButtons());
                replyMessage.setParseMode("HTML");
                botState = BotState.FILLING_TOUR;
                userOrderCache.saveUserOrder(userId, Order.builder().userId(userId).chatId(chatId).build());
                ChatPermissions chatPermissions = new ChatPermissions();
                chatPermissions.setCanSendMessages(false);
//                SetChatPermissions setChatPermissions = new SetChatPermissions();
//                setChatPermissions.setChatId(chatId);
//                setChatPermissions.setPermissions(chatPermissions);

//                RestrictChatMember restrictChatMember=new RestrictChatMember();
//                restrictChatMember.setChatId(chatId);
//                restrictChatMember.setUserId(userId);
//                restrictChatMember.setPermissions(chatPermissions);
//                telegramBot.execute(restrictChatMember);

//                LeaveChat leaveChat=new LeaveChat();
//                leaveChat.setChatId(chatId);
//                telegramBot.execute(leaveChat);
                break;
            case "/stop":
                telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
//                botState = BotState.FILLING_PROFILE;
                break;
            default:
                System.out.println("Bax buraaaaaaaa");
                botState = userOrderCache.getUsersCurrentBotState(userId);
                replyMessage = botStateContext.processInputMessage(userOrderCache.getCurrentButtonTypeAndMessage(userId),botState, message);
                break;
        }

        userOrderCache.setUsersCurrentBotState(userId, botState);
        telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
        return replyMessage;
    }

    private List<BotApiMethod<?>> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        final int messageId = buttonQuery.getMessage().getMessageId();
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        Order userOrder = userOrderCache.getUserOrder(userId);

        if (buttonQuery.getData().startsWith("Lang")) {

            callBackAnswer.add(getLanguageType(buttonQuery, userOrder));
            callBackAnswer.add(new SendMessage(chatId, userOrder.getLanguage().name()));
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId,
                    userOrderCache, questionRepo.getFirstQuestionByLanguage(userOrder.getLanguage().name())));
            callBackAnswer.add(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId).setReplyMarkup(null));

        } else if (buttonQuery.getData().startsWith("Order")) {
            Question question = questionRepo.findById(userOrderCache.getQuestionIdAndNext(userId).getNext()).orElse(null);

            if (question == null) {
                userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
                callBackAnswer.add(new SendMessage(chatId, sendEndingMessage(userOrder)));
               return callBackAnswer;
            }

            findCallback(buttonQuery, userOrder, userOrderCache.getQuestionIdAndNext(userId));
            CurrentButtonTypeAndMessage currentButtonTypeAndMessage = userOrderCache.getCurrentButtonTypeAndMessage(userId);
            System.out.println(currentButtonTypeAndMessage.getQuestionType().name());

            callBackAnswer.add(new SendMessage(chatId, "<b>" + currentButtonTypeAndMessage.getMessage() + "</b>").setParseMode("HTML"));

            if (currentButtonTypeAndMessage.getQuestionType().equals(QuestionType.Button)) {
                callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId,
                        userOrderCache, question));
                callBackAnswer.add(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId).setReplyMarkup(null));

            } else {

                callBackAnswer.add(new DeleteMessage().setChatId(chatId).setMessageId(messageId));
                userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
            }
        } else {
            Question question = questionRepo.findById(userOrderCache.getQuestionIdAndNext(userId).getNext()).orElse(null);
            callBackAnswer=getDateTime(buttonQuery, userOrder);
            if (callBackAnswer.contains(null)){
                callBackAnswer = callBackAnswer.stream().filter(a->a!=null).collect(Collectors.toList());
                callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId,
                        userOrderCache, question));
            }

        }

        userOrderCache.saveUserOrder(userId, userOrder);
        System.out.println(userOrder);

        return callBackAnswer;


    }

//    public static String sendEndingMessage(Order userOrder) {
//        String text;
//        if (userOrder.getLanguage().name() == "AZ") {
//            text = "Əla,qısa zamanda sizə təkliflər göndərəcəyik.";
//        } else if (userOrder.getLanguage().name() == "RU") {
//            text = "Отлично, мы пришлем вам предложения в кратчайшие сроки";
//        } else {
//            text = "Excellent, we will send you suggestions as soon as possible";
//        }
//        return text;
//    }

    private List<BotApiMethod<?>> getDateTime(CallbackQuery buttonQuery, Order userOrder) {
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        int time = userOrderCache.getCalendarTime(buttonQuery.getFrom().getId());
        if (buttonQuery.getData().equals(">")) {
            time++;
            userOrderCache.setCalendarTime(buttonQuery.getFrom().getId(), time);
            callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
            callBackAnswer.add(new SendMessage().setChatId(buttonQuery.getMessage().getChatId()).setText("Calendar" + Emojis.Clock).
                    setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))));
        } else if (buttonQuery.getData().equals("<")) {
            if (time == 0) {
                callBackAnswer.add(sendAnswerCallbackQuery(getPrevCalendarMessage(userOrder), true, buttonQuery));
            } else {
                time--;
                userOrderCache.setCalendarTime(buttonQuery.getFrom().getId(), time);
                callBackAnswer.add(new SendMessage().setChatId(buttonQuery.getMessage().getChatId()).setText("Calendar" + Emojis.Clock).
                        setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now().plusMonths(time))));
                callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
            }

        } else if (Arrays.stream(WD).anyMatch(a -> a.equals(buttonQuery.getData())) || buttonQuery.getData().equals(IGNORE)) {
            callBackAnswer.add(sendAnswerCallbackQuery(sendIgnoreMessage(userOrder), true, buttonQuery));
        } else {
            findCallback(buttonQuery, userOrder, userOrderCache.getQuestionIdAndNext(buttonQuery.getFrom().getId()));
            callBackAnswer.add(new DeleteMessage().setChatId(buttonQuery.getMessage().getChatId()).setMessageId(buttonQuery.getMessage().getMessageId()));
            callBackAnswer.add(new SendMessage(buttonQuery.getMessage().getChatId(),
                    "<b>" + userOrderCache.getCurrentButtonTypeAndMessage(buttonQuery.getFrom().getId()).getMessage() + "</b>")
                    .setParseMode("HTML"));
            callBackAnswer.add(null);
        }

        return callBackAnswer;
    }

//    private String sendIgnoreMessage(Order userOrder) {
//        String text;
//        if (userOrder.getLanguage().name() == "AZ") {
//            text = "Yalnız tarixləri seçə bilərsiz";
//        } else if (userOrder.getLanguage().name() == "RU") {
//            text = "Вы можете выбрать только даты";
//        } else {
//            text = "You can only select dates";
//        }
//        return text;
//    }
//
//    private String getPrevCalendarMessage(Order userOrder) {
//        String text;
//        if (userOrder.getLanguage().name() == "AZ") {
//            text = "Yalnız indiki və gələcək zamanı seçə bilərsiz";
//        } else if (userOrder.getLanguage().name() == "RU") {
//            text = "Вы можете выбрать только настоящее и будущее время";
//        } else {
//            text = "You can only choose the present and future tenses";
//        }
//        return text;
//    }


    @SneakyThrows
    private BotApiMethod<?> findCallback(CallbackQuery buttonQuery, Order userOrder, QuestionIdAndNext questionIdAndNext) {
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;
        List<QuestionAction> questionActions = questionActionRepo.findQuestionActionsByNext(questionIdAndNext.getNext());
        Class<?> order = userOrder.getClass();
        for (var item : questionActions) {
            Field field = order.getDeclaredField(item.getKeyword());
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (buttonQuery.getData().equals(item.getKeyword() + item.getId())) {

                if (item.getType().equals(QuestionType.Button)) {
                    QuestionAction questionAction = questionActionRepo.findById(item.getId()).get();

                    Object text = questionAction.getText();

                    if (isPrimitive(type)) {
                        Object boxed = boxPrimitiveClass(type,text.toString());
                        field.set(userOrder, boxed);
                    }else{
                        field.set(userOrder, text);
                    }
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(item.getType())
                            .message(text.toString()).build());


                } else {
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(item.getType())
                            .message(buttonQuery.getMessage().getText()).build());
                }


                break;
            } else if (item.getType().equals(QuestionType.Button_Calendar)) {
                Object text = buttonQuery.getData();
                DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
                DateTime dateTime = FORMATTER.parseDateTime(text.toString());
                LocalDate localDate = dateTime.toLocalDate();

                field.set(userOrder, localDate);
                userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(item.getType())
                        .message(text.toString()).build());
            }
        }

        return callBackAnswer;
    }

    public static boolean isPrimitive(Class<?> type) {
        return (type == int.class || type == long.class || type == double.class || type == float.class
                || type == boolean.class || type == byte.class || type == char.class || type == short.class);
    }

    public static Object boxPrimitiveClass(Class<?> type,String text) {
        if (type == int.class) {

            return Integer.valueOf(text);
        } else if (type == long.class) {
            return Long.valueOf(text);
        } else if (type == double.class) {
            return Double.valueOf(text);
        } else if (type == float.class) {
            return Float.valueOf(text);
        } else if (type == byte.class) {
            return Byte.valueOf(text);
        } else if (type == short.class) {
            return Short.valueOf(text);
        } else {
            String string = "class '" + type.getName() + "' is not a primitive";
            throw new IllegalArgumentException(string);
        }
    }


    private BotApiMethod<?> getTravelType(CallbackQuery buttonQuery, Order userOrder) {

        final int userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer = null;


        if (buttonQuery.getData().equals("LangButtonAz")) {
            userOrder.setLanguage(Languages.AZ);
            callBackAnswer = sendAnswerCallbackQuery("Botun dili Azərbaycan dili olaraq təyin olundu", true, buttonQuery);
        } else if (buttonQuery.getData().equals("LangButtonRu")) {
            userOrder.setLanguage(Languages.RU);
            callBackAnswer = sendAnswerCallbackQuery("Язык Ботуна был определен как русский", true, buttonQuery);
        } else if (buttonQuery.getData().equals("LangButtonEn")) {
            userOrder.setLanguage(Languages.EN);
            callBackAnswer = sendAnswerCallbackQuery("Bot's language was designated as English", true, buttonQuery);
        }
        userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);

        return callBackAnswer;
    }

    private BotApiMethod<?> getLanguageType(CallbackQuery buttonQuery, Order userOrder) {

        final int userId = buttonQuery.getFrom().getId();

        BotApiMethod<?> callBackAnswer = null;

        if (buttonQuery.getData().equals("LangButtonAz")) {
            userOrder.setLanguage(Languages.AZ);
            callBackAnswer = sendAnswerCallbackQuery("Botun dili Azərbaycan dili olaraq təyin olundu", true, buttonQuery);
        } else if (buttonQuery.getData().equals("LangButtonRu")) {
            userOrder.setLanguage(Languages.RU);
            callBackAnswer = sendAnswerCallbackQuery("Язык Ботуна был определен как русский", true, buttonQuery);
        } else if (buttonQuery.getData().equals("LangButtonEn")) {
            userOrder.setLanguage(Languages.EN);
            callBackAnswer = sendAnswerCallbackQuery("Bot's language was designated as English", true, buttonQuery);
        }
        userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
        userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(QuestionType.Button)
                .message(userOrder.getLanguage().name()).build());
        return callBackAnswer;
    }


    private synchronized AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

//    @SneakyThrows
//    public File getUsersProfile(int userId) {
//        Student userProfileData = userDataCache.getUserProfileData(userId);
//        File profileFile = ResourceUtils.getFile("src/main/resources/static/docs/users_profile.txt");
//
//        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
//             BufferedWriter bw = new BufferedWriter(fw)) {
//            bw.write(userProfileData.toString());
//        }
//
//
//        return profileFile;
//
//    }


}
