package com.mycode.tourapptelegrambot.messages;

import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.utils.Emojis;

/**
 * This class for static responses to user
 */

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
        return text + Emojis.SUCCESS_MARK;
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
        return text + Emojis.Times;
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
        return text + Emojis.Times;
    }

    public static String getContinueMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Sizə veriləcək sual yoxdur";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Нет вопросов к тебе";
        } else {
            text = "There is no question for you";
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

    public static String getStartCacheMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Yenidən başlamaq üçün ilk öncə <b> stop </b> yazmalısan\n /stop - prosesi bitirmək üçün" +
                    " \n /continue-suallarınıza davam etmək üçün";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Для перезапуска сначала необходимо ввести <b> /stop </b>\n /stop - завершить процесс" +
                    " \n /continue-чтобы продолжить ваши вопросы";
        } else {
            text = "To restart, you must first enter <b> /stop </b>\n /stop - end the process" +
                    " \n /continue-to continue your questions";
        }
        return text;
    }

    public static String getStopContinueCacheMessage() {
        String text = "Yenidən başlamaq üçün ilk öncə <b> /start </b> və ya <b> /new </b> yazmalısan\n/new - prosesi yenidən başlamaq üçün";
        return text;
    }

    public static String getDefaultCacheMessage(Order userOrder) {
        String text;
        if (userOrder.getLanguage().name() == "AZ") {
            text = "Yenidən başlamaq üçün ilk öncə <b> /start </b> və ya <b> /new </b> yazmalısan\n/new - prosesi yenidən başlamaq üçün" +
                    "\n /stop - prosesi bitirmək üçün ";
        } else if (userOrder.getLanguage().name() == "RU") {
            text = "Для перезапуска необходимо сначала ввести <b> /start </b> или <b> /new </b>\n/new - перезапустить процесс" +
                    "\n /stop - завершить процесс";
        } else {
            text = "To restart, you must first type <b> /start </b> or <b> /new </b>\n/new - to restart the process" +
                    "\n /stop - end the process";
        }
        return text;
    }


}
