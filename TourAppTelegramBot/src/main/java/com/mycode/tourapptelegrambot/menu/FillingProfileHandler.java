package com.mycode.tourapptelegrambot.menu;


import com.mycode.tourapptelegrambot.bot.botfacace.InputMessageHandler;
import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.rabbitmq.rabbitservice.RabbitMQService;
import com.mycode.tourapptelegrambot.redis.RedisCache.*;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentBotState;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.inlineButtons.UniversalInlineButtons;
import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import com.mycode.tourapptelegrambot.repositories.OrderRepo;
import com.mycode.tourapptelegrambot.repositories.QuestionActionRepo;
import com.mycode.tourapptelegrambot.repositories.QuestionRepo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.sendEndingMessage;

/**
 * This class for when bot ask question without inline keyboard button
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {

    private final QuestionIdAndNextCache questionIdAndNextCache;
    private final ButtonAndMessageCache buttonMessageCache;
    private final MessageBoolCache messageBoolCache;
    private final BotStateCache botStateCache;
    private final OrderCache orderCache;
    private final OfferCache offerCache;
    private final QuestionActionRepo questionActionRepo;
    private final QuestionRepo questionRepo;
    private final OrderRepo orderRepo;
    private final RabbitMQService rabbitMQService;


    public FillingProfileHandler(QuestionActionRepo questionActionRepo,
                                 QuestionRepo questionRepo, OrderRepo orderRepo, QuestionIdAndNextCache questionIdAndNextCache,
                                 ButtonAndMessageCache buttonMessageCache, MessageBoolCache messageBoolCache, BotStateCache botStateCache,
                                 OrderCache orderCache,RabbitMQService rabbitMQService,OfferCache offerCache) {
        this.questionActionRepo = questionActionRepo;
        this.questionRepo = questionRepo;
        this.orderRepo = orderRepo;
        this.questionIdAndNextCache = questionIdAndNextCache;
        this.buttonMessageCache = buttonMessageCache;
        this.messageBoolCache = messageBoolCache;
        this.botStateCache = botStateCache;
        this.orderCache = orderCache;
        this.rabbitMQService=rabbitMQService;
        this.offerCache=offerCache;
    }

    @Override
    public SendMessage handle(Message message) {
        if (botStateCache.get(message.getFrom().getId()).getBotState().equals(BotState.FILLING_TOUR)) {
            botStateCache.save(CurrentBotState.builder().userId(message.getFrom().getId()).botState(BotState.FILLING_TOUR).build());
        }
        return processUsersInput(message);
    }

    /**
     * When bot not ask question with inline keyboard button program set current bot state FILLING_TOUR
     * And when user input message has text program checks this getHandlerName method of this class
     */

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_TOUR;
    }

    /**
     * This methods for process actions for user input
     */

    public SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        int messageId = inputMsg.getMessageId();
        String regex = questionIdAndNextCache.get(userId).getRegex();
        Order userOrder = orderCache.get(userId);
        BotState botState = botStateCache.get(userId).getBotState();

        return getReplyForBotState(botState, userId, chatId, messageId, usersAnswer, regex, userOrder, inputMsg);
    }

    private SendMessage getReplyForBotState(BotState botState, int userId, long chatId, int messageId, String usersAnswer,
                                            String regex, Order userOrder, Message inputMsg) {
        SendMessage replyToUser = null;
        if (botState.equals(BotState.VALIDATION)) {
            botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        }
        if (botState.equals(BotState.FILLING_TOUR)) {

            if (!Pattern.matches(regex, usersAnswer)) {
                replyToUser = new SendMessage(chatId, buttonMessageCache.get(userId).getMessage());
                botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.VALIDATION).build());
                processUsersInput(inputMsg);
            } else {
                replyToUser = mapToObject(userId, userOrder, usersAnswer);
                if (replyToUser != null) {
                    return replyToUser;
                }
                Question question = questionRepo.findById(questionIdAndNextCache.get(userId).getNext()).orElse(null);
                if (question != null) {
                    replyToUser = replyQuestionNotNull(userId, chatId, messageId, question, userOrder);
                } else {
                    replyToUser = replyQuestionNull(userId, chatId, userOrder);
                }
                System.out.println(userOrder);
            }
        }
        return replyToUser;
    }


    private SendMessage replyQuestionNull(int userId, long chatId, Order userOrder) {
        SendMessage sendMessage = new SendMessage(chatId, sendEndingMessage(userOrder));
        userOrder.setCreatedDate(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        userOrder.setExpiredDate(calendar.getTime());
        orderRepo.save(userOrder);
        rabbitMQService.send(userOrder);
        deleteCache(userId);
        return sendMessage;
    }

    private SendMessage replyQuestionNotNull(int userId, long chatId, int messageId, Question question, Order userOrder) {
        SendMessage sendMessage = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId,
                questionIdAndNextCache, question, buttonMessageCache, messageBoolCache);
        buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(QuestionType.Free_Text)
                .message(question.getQuestion()).build());
        botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        questionIdAndNextCache.save(getQuestionIdAndNextFromQuestion(question, userId));
        orderCache.save(CurrentOrder.builder().userId(userId).order(userOrder).build());
        return sendMessage;
    }

    /**
     * When last answered correctly clear @Redis cache
     */

    private void deleteCache(int userId) {
        botStateCache.delete(userId);
        buttonMessageCache.delete(userId);
        messageBoolCache.delete(userId);
        offerCache.delete(userId);
        questionIdAndNextCache.delete(userId);
        Languages languages = orderCache.get(userId).getLanguage();
        orderCache.delete(userId);
        orderCache.save(CurrentOrder.builder().userId(userId).order(Order.builder().language(languages).build()).build());
    }


    /**
     * This method for setting question info
     * Prev -Previous question id
     * Next -Next question id
     * QuestionId-Question Action entity's id
     * Regex-Question entity's field for validation
     * UserId-For @Redis cache
     */

    private QuestionIdAndNext getQuestionIdAndNextFromQuestion(Question question, int userId) {
        QuestionIdAndNext questionIdAndNext = new QuestionIdAndNext();
        for (var item : question.getQuestionActions()) {
            questionIdAndNext.setPrev(item.getQuestion().getId());
            questionIdAndNext.setNext(item.getNext());
            questionIdAndNext.setQuestionId(item.getId());
        }
        questionIdAndNext.setRegex(question.getRegex());
        questionIdAndNext.setUserId(userId);
        return questionIdAndNext;
    }


    /**
     * Mapping answer to object
     * This method map dynamically to object by keyword
     * Keyword comes from database and entity class filed name same as keyword
     */

    @SneakyThrows
    private SendMessage mapToObject(int userId, Order userOrder, String userAnswer) {

        QuestionIdAndNext questionIdAndNext = questionIdAndNextCache.get(userId);
        Class<?> order = userOrder.getClass();

        QuestionAction questionAction = questionActionRepo.findById(questionIdAndNext.getQuestionId()).get();
        Field field = order.getDeclaredField(questionAction.getKeyword());
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (isPrimitive(type)) {
            Object boxed;
            try {
                boxed = boxPrimitiveClass(type, userAnswer);
            } catch (Exception e) {
                return new SendMessage(userOrder.getChatId(), buttonMessageCache.get(userId).getMessage());
            }
            field.set(userOrder, boxed);
        } else {
            field.set(userOrder, userAnswer);
        }

        buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionAction.getType())
                .message(userAnswer).build());

        return null;
    }


}


