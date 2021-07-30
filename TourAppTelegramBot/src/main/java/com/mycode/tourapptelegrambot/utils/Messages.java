package com.mycode.tourapptelegrambot.utils;

import com.mycode.tourapptelegrambot.models.BotMessage;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import org.json.JSONObject;

public class Messages {
    public  static String getBotMessage(String keyword, String language, BotMessageRepo botMessageRepo) {
        BotMessage botMessage = botMessageRepo.getBotMessageByKeyword(keyword);
        if (botMessage==null){
            return "\uD83D\uDE04";
        }else{
            JSONObject message = new JSONObject(botMessage.getMessage());
            return message.getString(language);
        }

    }
}
