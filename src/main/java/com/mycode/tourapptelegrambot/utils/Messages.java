package com.mycode.tourapptelegrambot.utils;

import com.mycode.tourapptelegrambot.models.BotMessage;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import org.json.JSONObject;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote convert json to string
 */

public class Messages {
    public static String getBotMessage(String keyword, String language, BotMessageRepo botMessageRepo) {
        BotMessage botMessage = botMessageRepo.getBotMessageByKeyword(keyword);
        if (botMessage == null) {
            return Emojis.Times.toString();
        } else {
            JSONObject message = new JSONObject(botMessage.getMessage());
            try {
                return message.getString(language.toUpperCase());
            } catch (Exception e) {
                return Emojis.Times.toString();
            }
        }

    }
}
