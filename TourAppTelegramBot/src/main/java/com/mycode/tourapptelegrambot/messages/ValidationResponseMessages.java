package com.mycode.tourapptelegrambot.messages;

import com.mycode.tourapptelegrambot.models.Order;

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
        return text;
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
        return text;
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
        return text;
    }
}
