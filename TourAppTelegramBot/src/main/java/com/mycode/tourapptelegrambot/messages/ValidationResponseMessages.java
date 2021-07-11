package com.mycode.tourapptelegrambot.messages;

import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.utils.Emojis;

/** This class for static responses to user */

public class ValidationResponseMessages {

    public static String sendEndingMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Əla,qısa zamanda sizə təkliflər göndərəcəyik.";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Отлично, мы пришлем вам предложения в кратчайшие сроки";
        } else {
            text = "Excellent, we will send you suggestions as soon as possible";
        }
        return text+Emojis.SUCCESS_MARK;
    }

    public static String sendIgnoreMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Yalnız tarixləri seçə bilərsiz";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Вы можете выбрать только даты";
        } else {
            text = "You can only select dates";
        }
        return text+Emojis.Times;
    }

    public static String getPrevCalendarMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Yalnız indiki və gələcək zamanı seçə bilərsiz";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Вы можете выбрать только настоящее и будущее время";
        } else {
            text = "You can only choose the present and future tenses";
        }
        return text+Emojis.Times;
    }

    public static String getContinueMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Sizə veriləcək sual yoxdur";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Вы можете выбрать только настоящее и будущее время";
        } else {
            text = "You can only choose the present and future tenses";
        }
        return text;
    }

    public static String getBotLangMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Botun dili Azərbaycan dili olaraq təyin olundu";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Язык Ботуна был определен как русский";
        } else {
            text = "Bot's language was designated as English";
        }
        return text;
    }
}
