package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.cache.UserOrderCache;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.models.Question;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class UniversalInlineButtons {


    public SendMessage sendInlineKeyBoardMessage(int userId, long chatId, UserOrderCache userOrderCache, Question question) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(question.getQuestion());
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        Long next = null;
        Long questionId = null;
        for (var item : question.getQuestionActions()) {
            next = item.getNext();
            questionId=item.getId();
            if (!item.getType().equals(QuestionType.Free_Text)) {
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(item.getText());
                keyboardButton.setCallbackData(item.getKeyword() + item.getId());
                keyboardButtonsRow1.add(keyboardButton);
            }
        }

        System.out.println(next);
        System.out.println(questionId);
        userOrderCache.setQuestionIdAndNext(userId, QuestionIdAndNext.builder().questionId(questionId).next(next).build());


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }
}
