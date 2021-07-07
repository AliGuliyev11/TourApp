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
import org.joda.time.LocalDate;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;

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
//            return processCallbackQuery(callbackQuery);
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
                SetChatPermissions setChatPermissions = new SetChatPermissions();
                setChatPermissions.setChatId(chatId);
                setChatPermissions.setPermissions(chatPermissions);
//                telegramBot.execute(setChatPermissions);


                break;
            case "/stop":
                telegramBot.execute(new SendChatAction().setAction(ActionType.TYPING).setChatId(chatId));
//                botState = BotState.FILLING_PROFILE;
                break;
            default:
                botState = userOrderCache.getUsersCurrentBotState(userId);
                replyMessage = botStateContext.processInputMessage(botState, message);
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
//        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "İstəyirsizsə,əsas menudan istifadə edin");
        List<BotApiMethod<?>> callBackAnswer = new ArrayList<>();
        Order userOrder = userOrderCache.getUserOrder(userId);

        //Language choose buttons
        if (buttonQuery.getData().startsWith("Lang")) {

            callBackAnswer.add(getLanguageType(buttonQuery, userOrder));
            callBackAnswer.add(new SendMessage(chatId, userOrder.getLanguage().name()));
            callBackAnswer.add(new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId,
                    userOrderCache, questionRepo.getFirstQuestionByLanguage(userOrder.getLanguage().name())));
            callBackAnswer.add(new EditMessageReplyMarkup().setChatId(chatId).setMessageId(messageId).setReplyMarkup(null));

        } else if (buttonQuery.getData().startsWith("Order")) {
//            Nexte bax
            Question question = questionRepo.findById(userOrderCache.getQuestionIdAndNext(userId).getNext()).orElse(null);

            if (question == null) {
                //Son question olanda bura girsin
                userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
                callBackAnswer.add(new SendMessage(chatId, ""));
            }
//            if (question == null) {
//                System.out.println("Bura girir");
//                callBackAnswer.add(new SendMessage(chatId, "Adınızı qeyd edə bilərsiniz ?"));
//                findCallback(buttonQuery, userOrder, userOrderCache.getQuestionIdAndNext(userId));
//            } else {
//            System.out.println("Burdadir");

            //String message ve question type qaytar burda


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
            callBackAnswer = getDateTime(buttonQuery, userOrder);
        }

        userOrderCache.saveUserOrder(userId, userOrder);
        System.out.println(userOrder);

        return callBackAnswer;


    }

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

        }

        return callBackAnswer;
    }

    private String getPrevCalendarMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Yalnız indiki və gələcək zamanı seçə bilərsiz";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Вы можете выбрать только настоящее и будущее время";
        } else {
            text = "You can only choose the present and future tenses";
        }
        return text;
    }


    @SneakyThrows
    private BotApiMethod<?> findCallback(CallbackQuery buttonQuery, Order userOrder, QuestionIdAndNext questionIdAndNext) {
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;

        List<QuestionAction> questionActions = questionActionRepo.findQuestionActionsByNext(questionIdAndNext.getNext());
        Class<?> order = userOrder.getClass();
        for (var item : questionActions) {
            if (buttonQuery.getData().equals(item.getKeyword() + item.getId())) {
                if (item.getType().equals(QuestionType.Button)) {
                    QuestionAction questionAction = questionActionRepo.findById(item.getId()).get();

                    Object text = questionAction.getText();
                    Field field = order.getDeclaredField(item.getKeyword());
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    if (isPrimitive(type)) {
                        Class<?> boxed = boxPrimitiveClass(type);
                        text = boxed.cast(text);
                    }
                    field.set(userOrder, text);
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(item.getType())
                            .message(text.toString()).build());


                } else {
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(item.getType())
                            .message(buttonQuery.getMessage().getText()).build());
                }


                break;
            }
        }

        return callBackAnswer;
    }

    public static boolean isPrimitive(Class<?> type) {
        return (type == int.class || type == long.class || type == double.class || type == float.class
                || type == boolean.class || type == byte.class || type == char.class || type == short.class);
    }

    public static Class<?> boxPrimitiveClass(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        } else if (type == long.class) {
            return Long.class;
        } else if (type == double.class) {
            return Double.class;
        } else if (type == float.class) {
            return Float.class;
        } else if (type == boolean.class) {
            return Boolean.class;
        } else if (type == byte.class) {
            return Byte.class;
        } else if (type == char.class) {
            return Character.class;
        } else if (type == short.class) {
            return Short.class;
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
