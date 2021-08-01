package com.mycode.tourapptelegrambot.utils;


import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import org.joda.time.LocalDate;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mycode.tourapptelegrambot.utils.Messages.getBotMessage;

/**
 * This class for create calendar when QuestionType is Button_Calendar
 */


public class CalendarUtil {
    public static final String IGNORE = "ignore!@#$%^&";

    public InlineKeyboardMarkup generateKeyboard(LocalDate date, BotMessageRepo botMessageRepo, String languages) {

        if (date == null) {
            return null;
        }

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(createButton(IGNORE, new SimpleDateFormat("MMM yyyy").format(date.toDate())));
        keyboard.add(headerRow);
        List<String> WD=Arrays.asList(getBotMessage("weekdays", languages, botMessageRepo).split("[,]"));
        if (WD.size() != 7) {
            WD = Arrays.asList("M","T","W","T","F","ST","S");
        }
        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : WD) {
            daysOfWeekRow.add(createButton(IGNORE, day));
        }
        keyboard.add(daysOfWeekRow);

        LocalDate firstDay = date.dayOfMonth().withMinimumValue();

        int shift = firstDay.dayOfWeek().get() - 1;
        int daysInMonth = firstDay.dayOfMonth().getMaximumValue();
        int rows = ((daysInMonth + shift) % 7 > 0 ? 1 : 0) + (daysInMonth + shift) / 7;
        for (int i = 0; i < rows; i++) {
            keyboard.add(buildRow(firstDay, shift, date));
            firstDay = firstDay.plusDays(7 - shift);
            shift = 0;
        }

        List<InlineKeyboardButton> controlsRow = new ArrayList<>();
        controlsRow.add(createButton("<", "<"));
        controlsRow.add(createButton(">", ">"));
        keyboard.add(controlsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createButton(String callBack, String text) {
        return InlineKeyboardButton.builder().callbackData(callBack).text(text).build();
    }

    private List<InlineKeyboardButton> buildRow(LocalDate date, int shift, LocalDate localDate) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        int day = date.getDayOfMonth();
        LocalDate callbackDate = date;
        for (int j = 0; j < shift; j++) {
            row.add(createButton(IGNORE, " "));
        }
        for (int j = shift; j < 7; j++) {
            if (day <= (date.dayOfMonth().getMaximumValue())) {

                if (localDate.equals(LocalDate.now()) && day == LocalDate.now().getDayOfMonth()) {
                    row.add(createButton(callbackDate.toString(), "" + Emojis.Clock));
                    day++;
                } else if (date.isBefore(LocalDate.now()) && day < LocalDate.now().getDayOfMonth()) {
                    row.add(createButton(IGNORE, " "));
                    day++;
                } else {
                    row.add(createButton(callbackDate.toString(), Integer.toString(day++)));
                }
                callbackDate = callbackDate.plusDays(1);
            } else {
                row.add(createButton(IGNORE, " "));
            }
        }
        return row;
    }
}
