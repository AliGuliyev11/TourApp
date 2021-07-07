package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.utils.Emojis;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class AskLanguage {
    public static InlineKeyboardMarkup getLanguageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonAz = new InlineKeyboardButton().setText("AZ" + Emojis.Azerbaijan);
        InlineKeyboardButton buttonRu = new InlineKeyboardButton().setText("RU" + Emojis.Russian);
        InlineKeyboardButton buttonEn = new InlineKeyboardButton().setText("EN" + Emojis.English);

        //Every button must have callBackData, or else not work !
        buttonAz.setCallbackData("LangButtonAz");
        buttonRu.setCallbackData("LangButtonRu");
        buttonEn.setCallbackData("LangButtonEn");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonAz);
        keyboardButtonsRow1.add(buttonRu);
        keyboardButtonsRow1.add(buttonEn);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
