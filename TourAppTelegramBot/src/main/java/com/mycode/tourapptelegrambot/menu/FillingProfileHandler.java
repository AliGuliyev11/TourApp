package com.mycode.tourapptelegrambot.menu;


import com.mycode.tourapptelegrambot.bot.botfacace.InputMessageHandler;
import com.mycode.tourapptelegrambot.cache.UserOrderCache;
import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.inlineButtons.UniversalInlineButtons;
import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import com.mycode.tourapptelegrambot.repositories.OrderRepo;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import static com.mycode.tourapptelegrambot.bot.botfacace.TelegramFacade.*;


@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {


    private UserOrderCache userOrderCache;
    QuestionActionRepo questionActionRepo;
    QuestionRepo questionRepo;
    OrderRepo orderRepo;

    public FillingProfileHandler(UserOrderCache userDataCache, QuestionActionRepo questionActionRepo,
                                 QuestionRepo questionRepo, OrderRepo orderRepo) {
        this.userOrderCache = userDataCache;
        this.questionActionRepo = questionActionRepo;
        this.questionRepo = questionRepo;
        this.orderRepo = orderRepo;
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
                replyToUser = mapToObject(userId, userOrder, usersAnswer);
                if (replyToUser != null) {
                    return replyToUser;
                }
                Question question = questionRepo.findById(userOrderCache.getQuestionIdAndNext(userId).getNext()).orElse(null);
                if (question != null) {

                    replyToUser = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, userOrderCache, question);
                    userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(QuestionType.Free_Text)
                            .message(question.getQuestion()).build());
                    userOrderCache.setUsersCurrentBotState(userId, BotState.FILLING_TOUR);
                    userOrderCache.setQuestionIdAndNext(userId, getQuestionIdAndNextFromQuestion(question));
                    userOrderCache.saveUserOrder(userId, userOrder);
                } else {
                    replyToUser = new SendMessage(chatId, sendEndingMessage(userOrder));
                    userOrder.setCreatedDate(LocalDateTime.now());
                    userOrder.setExpiredDate(LocalDateTime.now().plusHours(24));
                    orderRepo.save(userOrder);
                    userOrderCache.saveUserOrder(userId, null);
                }
                System.out.println(userOrder);
            }
        }
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
    private SendMessage mapToObject(int userId, Order userOrder, String userAnswer) {
        SendMessage callBackAnswer = null;

        QuestionIdAndNext questionIdAndNext = userOrderCache.getQuestionIdAndNext(userId);
        Class<?> order = userOrder.getClass();

        QuestionAction questionAction = questionActionRepo.findById(questionIdAndNext.getQuestionId()).get();
        Object text = userAnswer;
        Field field = order.getDeclaredField(questionAction.getKeyword());
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (isPrimitive(type)) {
            Object boxed = null;
            try {
                boxed = boxPrimitiveClass(type, text.toString());
            } catch (Exception e) {
                return new SendMessage(userOrder.getChatId(), userOrderCache.getCurrentButtonTypeAndMessage(userId).getMessage());
            }
            field.set(userOrder, boxed);
        } else {
            field.set(userOrder, text);
        }

        userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(questionAction.getType())
                .message(text.toString()).build());


        return callBackAnswer;
    }


}



