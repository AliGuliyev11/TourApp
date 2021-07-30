package com.mycode.tourapptelegrambot.menu;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.bot.botfacade.InputMessageHandler;
import com.mycode.tourapptelegrambot.dto.QuestionAction.QAConverter;
import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.rabbitmq.orderOfferSend.rabbitservice.RabbitMQService;
import com.mycode.tourapptelegrambot.redis.RedisCache.*;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentBotState;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.inlineButtons.UniversalInlineButtons;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import com.mycode.tourapptelegrambot.repositories.QuestionActionRepo;
import com.mycode.tourapptelegrambot.repositories.QuestionRepo;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * This class for when bot ask question without inline keyboard button
 *
 * @author Ali Guliyev
 * @version 1.0
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
    private final RabbitMQService rabbitMQService;
    private final UserRepo userRepo;
    private final LocaleMessageService messageService;


    public FillingProfileHandler(QuestionActionRepo questionActionRepo,
                                 QuestionRepo questionRepo, QuestionIdAndNextCache questionIdAndNextCache,
                                 ButtonAndMessageCache buttonMessageCache, MessageBoolCache messageBoolCache, BotStateCache botStateCache,
                                 OrderCache orderCache, RabbitMQService rabbitMQService, OfferCache offerCache, UserRepo userRepo,
                                 LocaleMessageService messageService) {
        this.questionActionRepo = questionActionRepo;
        this.questionRepo = questionRepo;
        this.questionIdAndNextCache = questionIdAndNextCache;
        this.buttonMessageCache = buttonMessageCache;
        this.messageBoolCache = messageBoolCache;
        this.botStateCache = botStateCache;
        this.orderCache = orderCache;
        this.rabbitMQService = rabbitMQService;
        this.offerCache = offerCache;
        this.userRepo = userRepo;
        this.messageService = messageService;
    }

    /**
     * This method for handle user message
     *
     * @param message sended message by user
     * @return SendMessage
     */

    @Override
    public SendMessage handle(Message message) {
        if (botStateCache.get(message.getFrom().getId()).getBotState().equals(BotState.FILLING_TOUR)) {
            botStateCache.save(CurrentBotState.builder().userId(message.getFrom().getId()).botState(BotState.FILLING_TOUR).build());
        }
        return processUsersInput(message);
    }

    /**
     * When bot not ask question with inline keyboard button program set current bot state FILLING_TOUR
     *
     * @return BotState
     * @apiNote And when user input message has text program checks this getHandlerName method of this class
     */

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_TOUR;
    }

    /**
     * This methods for process actions for user input
     *
     * @param inputMsg message which sended by user
     * @return SendMessage
     */

    public SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer;

        if (inputMsg.hasContact()) {
            usersAnswer = inputMsg.getContact().getPhoneNumber().replaceAll("[\\s]", "");
        } else {
            usersAnswer = inputMsg.getText();
        }
        Long userId = inputMsg.getFrom().getId();
        String chatId = String.valueOf(inputMsg.getChatId());
        int messageId = inputMsg.getMessageId();
        String regex = questionIdAndNextCache.get(userId).getRegex();
        CurrentOrder userOrder = orderCache.get(userId);
        BotState botState = botStateCache.get(userId).getBotState();

        return getReplyForBotState(botState, userId, chatId, messageId, usersAnswer, regex, userOrder, inputMsg);
    }

    /**
     * This method for sending message with the help of current bot state
     *
     * @param botState    cached bot state
     * @param userId      current user id
     * @param chatId      private chat id
     * @param messageId   current mesage id
     * @param usersAnswer user answer to bot question
     * @param regex       question validation regex
     * @param userOrder   current cached user order
     * @param inputMsg    current message
     * @return SendMessage
     */

    private SendMessage getReplyForBotState(BotState botState, Long userId, String chatId, int messageId, String usersAnswer,
                                            String regex, CurrentOrder userOrder, Message inputMsg) {
        SendMessage replyToUser = null;
        if (botState.equals(BotState.VALIDATION)) {
            botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        }
        if (botState.equals(BotState.FILLING_TOUR)) {
            System.out.println(usersAnswer);
            if (!Pattern.matches(regex, usersAnswer)) {
                replyToUser = new SendMessage(chatId, buttonMessageCache.get(userId).getMessage());
                botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.VALIDATION).build());
                processUsersInput(inputMsg);
            } else {
                if (buttonMessageCache.get(userId).getQuestionType().equals(QuestionType.Button_Keyboard)) {
                    MyUser myUser = userRepo.findById(userId).get();
                    myUser.setPhoneNumber(usersAnswer);
                    userRepo.save(myUser);
                } else {
                    replyToUser = mapToObject(userId, userOrder, usersAnswer);
                    if (replyToUser != null) {
                        return replyToUser;
                    }
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

    /**
     * When question entity hasn't next,program enters this methods
     *
     * @param userId    delete current cache of user
     * @param chatId    send ending message to user from bot
     * @param userOrder add current user order to database
     * @return SendMessage
     */

    private SendMessage replyQuestionNull(Long userId, String chatId, CurrentOrder userOrder) {
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setRemoveKeyboard(true);
        SendMessage sendMessage = SendMessage.builder().chatId(chatId)
                .text(messageService.getMessage("ending.msg", userOrder.getLanguages(), Emojis.SUCCESS_MARK))
                .replyMarkup(replyKeyboardRemove).build();
        userOrder.getOrder().put("createdDate", new Date().toString());
        rabbitMQService.send(userOrder.getOrder());
        deleteCache(userId);
        return sendMessage;
    }

    /**
     * When question entity has next,program enters this methods
     *
     * @param userId    checking current cache
     * @param chatId    sending message to user from bot
     * @param question  its current question given by bot
     * @param messageId program catch this id for checking message statement and when user enters incorrect input delete this message
     * @param userOrder add current user order to cache
     * @return SendMessage
     */
    private SendMessage replyQuestionNotNull(Long userId, String chatId, int messageId, Question question, CurrentOrder userOrder) {
        JSONObject jsonObject = new JSONObject(question.getQuestion());
        SendMessage sendMessage = new UniversalInlineButtons().sendInlineKeyBoardMessage(userId, chatId, messageId,
                questionIdAndNextCache, question, buttonMessageCache, messageBoolCache, messageService, userOrder.getLanguages());
        buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(buttonMessageCache.get(userId).getQuestionType())
                .message(jsonObject.getString(userOrder.getLanguages().name().toUpperCase())).build());
        botStateCache.save(CurrentBotState.builder().userId(userId).botState(BotState.FILLING_TOUR).build());
        questionIdAndNextCache.save(getQuestionIdAndNextFromQuestion(question, userId));
        orderCache.save(CurrentOrder.builder().userId(userId).languages(userOrder.getLanguages()).order(userOrder.getOrder()).build());
        return sendMessage;
    }

    /**
     * When last answered correctly clear @Redis cache
     *
     * @param userId clear cache of current user
     */

    private void deleteCache(Long userId) {
        botStateCache.delete(userId);
        buttonMessageCache.delete(userId);
        messageBoolCache.delete(userId);
        offerCache.delete(userId);
        questionIdAndNextCache.delete(userId);
        Languages languages = orderCache.get(userId).getLanguages();
        orderCache.delete(userId);
        orderCache.save(CurrentOrder.builder().userId(userId).languages(languages).order(null).build());
    }


    /**
     * This method for setting question info
     *
     * @param userId   for @Redis cache
     * @param question current question
     * @return QuestionIdAndNext cache DTO
     * @apiNote Prev -Previous question id
     * Next -Next question id
     * QuestionId-Question Action entity's id
     * Regex-Question entity's field for validation
     */

    private QuestionIdAndNext getQuestionIdAndNextFromQuestion(Question question, Long userId) {
        QuestionIdAndNext questionIdAndNext = new QuestionIdAndNext();
        QuestionAction questionAction = question.getQuestionActions();
        questionIdAndNext.setPrev(questionAction.getQuestion().getId());
        questionIdAndNext.setNext(questionAction.getNext());
        questionIdAndNext.setQuestionId(questionAction.getId());
        questionIdAndNext.setRegex(question.getRegex());
        questionIdAndNext.setUserId(userId);
        return questionIdAndNext;
    }


    /**
     * Mapping answer to object
     *
     * @param userId     for get data from cache
     * @param userOrder  get field dynamically and set matched keyword to Order entity
     * @param userAnswer data for setting to entity
     * @return SendMessage
     * @apiNote This method map dynamically to object by keyword
     * Keyword comes from database and entity class filed name same as keyword
     */

    @SneakyThrows
    private SendMessage mapToObject(Long userId, CurrentOrder userOrder, String userAnswer) {
        QuestionIdAndNext questionIdAndNext = questionIdAndNextCache.get(userId);
        QuestionAction questionAction = questionActionRepo.findById(questionIdAndNext.getQuestionId()).get();

        JSONObject jsonObject = new JSONObject(questionAction.getText());
        JSONObject convertedQuestionAction = jsonObject.getJSONObject(userOrder.getLanguages().name().toUpperCase());
        ObjectMapper objectMapper = new ObjectMapper();
        QAConverter qaConverter = objectMapper.readValue(convertedQuestionAction.toString(), QAConverter.class);

        userOrder.getOrder().put(questionAction.getKeyword(), userAnswer);
        buttonMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId)
                .questionType(QuestionType.valueOf(qaConverter.questionAction.get(0).buttonType))
                .message(userAnswer).build());

        return null;
    }


}


