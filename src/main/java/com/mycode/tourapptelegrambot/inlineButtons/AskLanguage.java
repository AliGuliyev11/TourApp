package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.models.Language;
import com.mycode.tourapptelegrambot.utils.Emojis;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class for just /start case
 * When user send /start message program enters this method for creating language inline keyboard button
 *
 * @author Ali Guliyev
 * @version 1.0
 */

public class AskLanguage {

    /**
     * @param languages which has enough data
     * @return InlineKeyboardMarkup
     */
    public static InlineKeyboardMarkup getLanguageButtons(LinkedList<Language> languages) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        int i = 0;
        if (languages.size() <= 5) {
            for (var item : languages) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(item.getLang() + item.getEmoji());
                button.setCallbackData("LangButton" + item.getLang());
                keyboardButtonsRow1.add(button);
            }
            rowList.add(keyboardButtonsRow1);
        } else {
            for (var item : languages) {
                keyboardButtonsRow1 = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(item.getLang() + item.getEmoji());
                button.setCallbackData("LangButton" + item.getLang());
                keyboardButtonsRow1.add(button);
                rowList.add(keyboardButtonsRow1);
            }
        }

        inlineKeyboardMarkup.setKeyboard(rowList);


        return inlineKeyboardMarkup;
    }

}
