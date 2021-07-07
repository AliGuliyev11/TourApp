package com.mycode.tourapptelegrambot.cache;


import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.models.Order;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    Order getUserOrder(int userId);

    void saveUserOrder(int userId, Order userOrder);

    void setQuestionIdAndNext(int userId, QuestionIdAndNext questionIdAndNext);

    QuestionIdAndNext getQuestionIdAndNext(int userId);

    void setCurrentButtonTypeAndMessage(int userId, CurrentButtonTypeAndMessage currentButtonTypeAndMessage);
    CurrentButtonTypeAndMessage getCurrentButtonTypeAndMessage(int userId);
    void setCalendarTime(int userId,int time);
    Integer getCalendarTime(int userId);
}
