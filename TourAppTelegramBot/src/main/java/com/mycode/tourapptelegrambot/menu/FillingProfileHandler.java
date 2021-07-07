package com.mycode.tourapptelegrambot.menu;


import com.mycode.tourapptelegrambot.bot.botfacace.InputMessageHandler;
import com.mycode.tourapptelegrambot.cache.UserOrderCache;
import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.QuestionType;
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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

import static com.mycode.tourapptelegrambot.bot.botfacace.TelegramFacade.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.bot.botfacace.TelegramFacade.isPrimitive;


@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {

    private static final String EMAIL_REGEX = "([a-zA-Z0-9_.+-])+\\@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})";
    private static final String PHONE_REGEX = "[+]{1}[9]{2}[4]{1}(([5]([0]|[1]|[5]))|([7]([0]|[7]))|([9]([9])))[1-9][0-9]{6}";
    private static final String ONLY_WORD_REGEX = "[a-zA-Z]+";
    //
    private UserOrderCache userOrderCache;
    QuestionActionRepo questionActionRepo;
    QuestionRepo questionRepo;
//    private ReplyMessagesService messagesService;

    public FillingProfileHandler(UserOrderCache userDataCache, QuestionActionRepo questionActionRepo, QuestionRepo questionRepo) {
        this.userOrderCache = userDataCache;
//        this.messagesService = messagesService;
        this.questionActionRepo = questionActionRepo;
        this.questionRepo = questionRepo;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userOrderCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_TOUR)) {
            userOrderCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.FILLING_TOUR);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_TOUR;
    }

    public SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        Order userOrder = userOrderCache.getUserOrder(userId);
//
        BotState botState = userOrderCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;
        if (botState.equals(BotState.VALIDATION)) {
            userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
        }

        if (botState.equals(BotState.FILLING_TOUR)) {
            if (usersAnswer.length() > 50) {
                replyToUser = new SendMessage(chatId, userOrderCache.getCurrentButtonTypeAndMessage(userId).getMessage());
                userOrderCache.setUsersCurrentBotState(userId, BotState.VALIDATION);
                processUsersInput(inputMsg);
            } else {
                mapToObject(userId, userOrder, usersAnswer);
                Question question = questionRepo.findById(userOrderCache.getQuestionIdAndNext(userId).getNext()).orElse(null);
                if (question != null) {
                    replyToUser = new SendMessage(chatId, question.getQuestion());
                    userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(QuestionType.Free_Text)
                            .message(question.getQuestion()).build());
                    userOrderCache.setQuestionIdAndNext(userId,getQuestionIdAndNextFromQuestion(question));
                } else {
                    replyToUser = new SendMessage().setChatId(chatId).setText("Calendar"+ Emojis.Clock).setReplyMarkup(new CalendarUtil().generateKeyboard(new LocalDate()));
                }
                System.out.println(userOrder);
            }
        }

//        if (botState.equals(BotState.PROFILE_FILLED)) {
//
//            if (!Pattern.matches(EMAIL_REGEX, usersAnswer)) {
//                replyToUser = messagesService.getReplyMessage(chatId, "reply.falseEmailFormat");
//                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_EMAIL_FORMAT);
//                processUsersInput(inputMsg);
//            } else {
//                profileData.setEmail(usersAnswer);
//                profileData.setUserId(userId);
//                profileData.setChatId(chatId);
//                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
//                replyToUser = messagesService.getReplyMessage(chatId, "reply.profileFilled");
//                replyToUser.setParseMode("HTML");
//                studentRepo.save(profileData);
//                userDataCache.saveUserProfileData(userId, profileData);
//            }
//
//        }

//        userDataCache.saveUserProfileData(userId, profileData);
        return replyToUser;
    }

    private QuestionIdAndNext getQuestionIdAndNextFromQuestion(Question question) {
        QuestionIdAndNext questionIdAndNext = new QuestionIdAndNext();
        for (var item : question.getQuestionActions()) {
            questionIdAndNext.setNext(item.getNext());
            questionIdAndNext.setQuestionId(item.getId());
        }
        return questionIdAndNext;
    }

    @SneakyThrows
    private BotApiMethod<?> mapToObject(int userId, Order userOrder, String userAnswer) {
        BotApiMethod<?> callBackAnswer = null;

        QuestionIdAndNext questionIdAndNext = userOrderCache.getQuestionIdAndNext(userId);
//        List<QuestionAction> questionActions = questionActionRepo.findQuestionActionsByNext(questionIdAndNext.getNext());
        Class<?> order = userOrder.getClass();
//        for (var item : questionActions) {
//            if (buttonQuery.getData().equals(item.getKeyword() + item.getId())) {
        QuestionAction questionAction = questionActionRepo.findById(questionIdAndNext.getQuestionId()).get();
        System.out.println("Bes bura " + questionAction.getText());
        Object text = userAnswer;
        Field field = order.getDeclaredField(questionAction.getKeyword());
        System.out.println("Filed name:" + field.getName());
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (isPrimitive(type)) {
            Class<?> boxed = boxPrimitiveClass(type);
            text = boxed.cast(text);
        }
        field.set(userOrder, text);
        userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(questionAction.getType())
                .message(text.toString()).build());

//                break;
//            }
//        }

        return callBackAnswer;
    }


}



