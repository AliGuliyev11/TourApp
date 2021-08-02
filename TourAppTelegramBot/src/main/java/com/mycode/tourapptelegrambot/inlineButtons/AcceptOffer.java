package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.mycode.tourapptelegrambot.utils.Messages.getBotMessage;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

public class AcceptOffer {

    /**
     * This class for creating Inline keyboard for accept button
     *
     * @param order          current order
     * @param offerId        agent's offer id
     * @param botMessageRepo messages which comes from DB
     * @return InlineKeyboardMarkup
     */

    public static InlineKeyboardMarkup getAcceptButtons(Long offerId, CurrentOrder order, BotMessageRepo botMessageRepo) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(getBotMessage("accept.offer", order.getLanguages(), botMessageRepo));

        accept.setCallbackData("Offer-" + offerId);

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }
}
