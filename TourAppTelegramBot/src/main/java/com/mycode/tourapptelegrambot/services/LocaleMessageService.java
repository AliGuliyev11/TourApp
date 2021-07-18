package com.mycode.tourapptelegrambot.services;

import com.mycode.tourapptelegrambot.enums.Languages;
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


    public String getMessage(String message, Languages languages) {


        if (languages == null) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals(Languages.AZ)) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals(Languages.RU)) {
            locale = new Locale("ru", "RU");
        } else {
            locale = new Locale("en", "EN");
        }

        return messageSource.getMessage(message, null, locale);
    }

    public String getMessage(String message, Languages languages, Object... args) {
        if (languages == null) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals(Languages.AZ)) {
            locale = new Locale("az", "AZ");
        } else if (languages.equals(Languages.RU)) {
            locale = new Locale("ru", "RU");
        } else {
            locale = new Locale("en", "EN");
        }

        return messageSource.getMessage(message, args, locale);
    }

}
