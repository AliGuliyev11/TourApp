package com.mycode.tourapptelegrambot.menu;


import com.mycode.tourapptelegrambot.bot.botfacace.InputMessageHandler;
import com.mycode.tourapptelegrambot.enums.Languages;
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
import java.util.regex.Pattern;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.sendEndingMessage;


@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {

    private final QuestionIdAndNextCache questionIdAndNextCache;
    private final ButtonAndMessageCache buttonMessageCache;
    private final MessageBoolCache messageBoolCache;
    private final BotStateCache botStateCache;
    private final OrderCache orderCache;
    private final QuestionActionRepo questionActionRepo;
    private final QuestionRepo questionRepo;
    private final OrderRepo orderRepo;


    public FillingProfileHandler(QuestionActionRepo questionActionRepo,
                                 QuestionRepo questionRepo, OrderRepo orderRepo, QuestionIdAndNextCache questionIdAndNextCache,
                                 ButtonAndMessageCache buttonMessageCache, MessageBoolCache messageBoolCache, BotStateCache botStateCache,
                                 OrderCache orderCache) {
        this.questionActionRepo = questionActionRepo;
        this.questionRepo = questionRepo;
        this.orderRepo = orderRepo;
        this.questionIdAndNextCache = questionIdAndNextCache;
        this.buttonMessageCache = buttonMessageCache;
        this.messageBoolCache = messageBoolCache;
        this.botStateCache = botStateCache;
        this.orderCache = orderCache;
    }

    @Override
    public SendMessage handle(Message message) {
        if (botStateCache.get(message.getFrom().getId()).getBotState().equals(BotState.FILLING_TOUR)) {
            botStateCache.save(CurrentBotState.builder().userId(message.getFrom().getId()).botState(BotState.FILLING_TOUR).build());
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_TOUR;
    }

    /** This methods for process actions for user input */

    public SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        int messageId = inputMsg.getMessageId();
        String regex = questionIdAndNextCache.get(userId).getRegex();
        Order userOrder = orderCache.get(userId);
//
        BotState botState = botStateCache.get(userId).getBotState();

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

                    replyToUser = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId,
                            questionIdAndNextCache, question, buttonMessageCache, messageBoolCache);
                    buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(QuestionType.Free_Text)
                            .message(question.getQuestion()).build());
                    botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
                    questionIdAndNextCache.save(getQuestionIdAndNextFromQuestion(question, userId));
                    orderCache.save(CurrentOrder.builder().userId(userId).order(userOrder).build());
                } else {
                    replyToUser = new SendMessage(chatId, sendEndingMessage(userOrder));
                    userOrder.setCreatedDate(LocalDateTime.now());
                    userOrder.setExpiredDate(LocalDateTime.now().plusHours(24));
                    orderRepo.save(userOrder);
                    /** Empty cache*/
                    deleteCache(userId);
                }
                System.out.println(userOrder);
            }
        }
        return replyToUser;
    }

    /** When last answered correctly clear @Redis cache  */

    private void deleteCache(int userId) {
        botStateCache.delete(userId);
        buttonMessageCache.delete(userId);
        messageBoolCache.delete(userId);
        questionIdAndNextCache.delete(userId);
        Languages languages=orderCache.get(userId).getLanguage();
        orderCache.delete(userId);
        orderCache.save(CurrentOrder.builder().userId(userId).order(Order.builder().language(languages).build()).build());
    }


    /** This method for setting question info
     * @Prev-Previous question id
     * @Next-Next question id
     * @QuestionId-Question Action entity's id
     * @Regex-Question entity's field for validation
     * @UserId-For @Redis cache*/

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


    /** Mapping answer to object
     * This method map dynamically to object by keyword
     * Keyword comes from database and entity class filed name same as keyword*/

    @SneakyThrows
    private SendMessage mapToObject(int userId, Order userOrder, String userAnswer) {
        SendMessage callBackAnswer = null;

        QuestionIdAndNext questionIdAndNext = questionIdAndNextCache.get(userId);
        Class<?> order = userOrder.getClass();

        QuestionAction questionAction = questionActionRepo.findById(questionIdAndNext.getQuestionId()).get();
        Object text = userAnswer;
        Field field = order.getDeclaredField(questionAction.getKeyword());
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (isPrimitive(type)) {
            Object boxed;
            try {
                boxed = boxPrimitiveClass(type, text.toString());
            } catch (Exception e) {
                return new SendMessage(userOrder.getChatId(), buttonMessageCache.get(userId).getMessage());
            }
            field.set(userOrder, boxed);
        } else {
            field.set(userOrder, text);
        }

        buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionAction.getType())
                .message(text.toString()).build());

        return callBackAnswer;
    }


}



