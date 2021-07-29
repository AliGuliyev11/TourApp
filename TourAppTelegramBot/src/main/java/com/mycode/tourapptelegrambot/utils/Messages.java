package com.mycode.tourapptelegrambot.utils;

import com.mycode.tourapptelegrambot.models.BotMessage;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import org.json.JSONObject;

public class Messages {
    public  static String getBotMessage(String keyword, String language, BotMessageRepo botMessageRepo) {
        BotMessage botMessage = botMessageRepo.getBotMessageByKeyword(keyword);
        JSONObject message = new JSONObject(botMessage.getMessage());
        return message.getString(language);
    }
}
