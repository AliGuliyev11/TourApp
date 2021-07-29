package com.mycode.tourapptelegrambot.services;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocaleMessageService {
    private Locale locale;
    private final MessageSource messageSource;

    public LocaleMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public String getMessage(String message, String languages) {


        if (languages == null) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals("AZ")) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals("Ru")) {
            locale = new Locale("ru", "RU");
        } else {
            locale = new Locale("en", "EN");
        }

        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, String languages, Object... args) {
        if (languages == null) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals("AZ")) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals("Ru")) {
            locale = new Locale("ru", "RU");
        }  else {
            locale = new Locale("en", "EN");
        }

        return messageSource.getMessage(message, args, locale);
    }

}
