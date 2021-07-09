package com.mycode.tourapptelegrambot.cache;

import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.MessageAndBoolean;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.models.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserOrderCache implements DataCache {
    private final Map<Integer, BotState> usersBotStates = new HashMap<>();
    private final Map<Integer, Order> usersOrder = new HashMap<>();
    private final Map<Integer, QuestionIdAndNext> questionIdAndNext = new HashMap<>();
    private final Map<Integer, CurrentButtonTypeAndMessage> currentButtonTypeAndMessage = new HashMap<>();
    private final Map<Integer,Integer> calendarTime=new HashMap<>();
    private final Map<Integer, MessageAndBoolean> lastMessage=new HashMap<>();


    @Override
    public void setLastMessage(int userId,MessageAndBoolean message) {
        lastMessage.put(userId,message);
    }

    @Override
    public MessageAndBoolean getLastMessage(int userId) {
        MessageAndBoolean message = lastMessage.get(userId);
        return message;
    }

    @Override
    public void setCalendarTime(int userId,int time) {
      calendarTime.put(userId,time);
    }

    @Override
    public Integer getCalendarTime(int userId) {
        Integer time = calendarTime.get(userId);
        if (time == null) {
            time =0;
        }

        return time;
    }


    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.ASK_DESTINY;
        }

        return botState;
    }

    @Override
    public Order getUserOrder(int userId) {
        Order userOrder = usersOrder.get(userId);
        if (userOrder == null) {
            userOrder = new Order();
        }
        return userOrder;
    }


    @Override
    public void saveUserOrder(int userId, Order userOrder) {
        this.usersOrder.put(userId, userOrder);
    }

    @Override
    public void setQuestionIdAndNext(int userId, QuestionIdAndNext questionIdAndNext) {
        this.questionIdAndNext.put(userId, questionIdAndNext);
    }

    @Override
    public QuestionIdAndNext getQuestionIdAndNext(int userId) {
        QuestionIdAndNext questionIdAndNext = this.questionIdAndNext.get(userId);
        if (questionIdAndNext == null) {
            questionIdAndNext = new QuestionIdAndNext();
        }
        return questionIdAndNext;
    }

    @Override
    public void setCurrentButtonTypeAndMessage(int userId, CurrentButtonTypeAndMessage currentButtonTypeAndMessage) {
        this.currentButtonTypeAndMessage.put(userId, currentButtonTypeAndMessage);
    }

    @Override
    public CurrentButtonTypeAndMessage getCurrentButtonTypeAndMessage(int userId) {
        CurrentButtonTypeAndMessage currentButtonTypeAndMessage = this.currentButtonTypeAndMessage.get(userId);
        if (currentButtonTypeAndMessage == null) {
            currentButtonTypeAndMessage = new CurrentButtonTypeAndMessage();
        }
        return currentButtonTypeAndMessage;
    }
}
