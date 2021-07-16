package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.mycode.tourapptelegrambot.messages.ValidationResponseMessages.acceptOffer;

public class AcceptOffer {

    public static InlineKeyboardMarkup getAcceptButtons(Long offerId, Order order, LocaleMessageService messageService) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonAz = new InlineKeyboardButton();
        buttonAz.setText(messageService.getMessage("accept.offer",order.getLanguage()));

        buttonAz.setCallbackData("Offer-" + offerId);

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonAz);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }
}
