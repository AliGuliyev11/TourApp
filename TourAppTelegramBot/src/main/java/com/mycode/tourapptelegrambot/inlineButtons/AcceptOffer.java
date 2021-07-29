package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.mycode.tourapptelegrambot.utils.Messages.getBotMessage;

public class AcceptOffer {

    public static InlineKeyboardMarkup getAcceptButtons(Long offerId, CurrentOrder order, BotMessageRepo botMessageRepo) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(getBotMessage("accept.offer",order.getLanguages(),botMessageRepo));

        accept.setCallbackData("Offer-" + offerId);

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }
}
