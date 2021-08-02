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

public class LoadMore {

    /**This static method for load more offer if offer count bigger than 5
     * @param order current order
     * @param botMessageRepo messages which comes from DB
     * @return InlineKeyboardMarkup
     * */

    public static InlineKeyboardMarkup getLoadButtons(CurrentOrder order, BotMessageRepo botMessageRepo) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton load = new InlineKeyboardButton();

        load.setText(getBotMessage("load.more",order.getLanguages(),botMessageRepo));

        load.setCallbackData("loadMore");

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(load);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);



        return inlineKeyboardMarkup;
    }
}
