package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.models.Language;
import com.mycode.tourapptelegrambot.utils.Emojis;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * This class for just /start case
 * When user send /start message program enters this method for creating language inline keyboard button
 * @author Ali Guliyev
 * @version 1.0
 */

public class AskLanguage {


    public static InlineKeyboardMarkup getLanguageButtons(List<Language> all) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        for (var item:all){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item.getLang()+item.getEmoji());
            button.setCallbackData("LangButton"+item.getLang());
            keyboardButtonsRow1.add(button);
        }

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }

}
