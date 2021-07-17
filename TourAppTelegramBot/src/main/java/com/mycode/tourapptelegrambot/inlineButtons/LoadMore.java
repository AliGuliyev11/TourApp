package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class LoadMore {
    public static InlineKeyboardMarkup getLoadButtons(Order order, LocaleMessageService localeMessageService) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton load = new InlineKeyboardButton();

        load.setText(localeMessageService.getMessage("load.more",order.getLanguage()));

        load.setCallbackData("loadMore");

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(load);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);



        return inlineKeyboardMarkup;
    }
}