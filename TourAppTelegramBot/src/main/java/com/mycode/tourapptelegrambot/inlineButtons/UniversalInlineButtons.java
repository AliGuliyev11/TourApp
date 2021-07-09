package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.cache.UserOrderCache;
import com.mycode.tourapptelegrambot.dto.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.dto.MessageAndBoolean;
import com.mycode.tourapptelegrambot.dto.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import org.joda.time.LocalDate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class UniversalInlineButtons {


    public SendMessage sendInlineKeyBoardMessage(int userId, long chatId,int messageId, UserOrderCache userOrderCache, Question question) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(question.getQuestion());
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        Long next = null;
        Long questionId = null;
        QuestionType questionType=null;
        for (var item : question.getQuestionActions()) {
            next = item.getNext();
            questionId=item.getId();
            questionType=item.getType();
            if (!item.getType().equals(QuestionType.Free_Text) && !item.getType().equals(QuestionType.Button_Calendar) && !item.getType().equals(QuestionType.Button_Numeric)) {
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(item.getText());
                keyboardButton.setCallbackData(item.getKeyword() + item.getId());
                keyboardButtonsRow1.add(keyboardButton);
                userOrderCache.setLastMessage(userId, MessageAndBoolean.builder().sendMessage(sendMessage).send(false).MessageId(messageId).build());

            }else if (item.getType().equals(QuestionType.Button_Calendar)){
                userOrderCache.setQuestionIdAndNext(userId, QuestionIdAndNext.builder().questionId(questionId).next(next).regex(question.getRegex()).build());
                userOrderCache.setLastMessage(userId, MessageAndBoolean.builder().sendMessage(sendMessage).send(false).MessageId(messageId).build());

                return sendMessage.setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now()));
            }
        }

        userOrderCache.setQuestionIdAndNext(userId, QuestionIdAndNext.builder().questionId(questionId).next(next).regex(question.getRegex()).build());
        userOrderCache.setCurrentButtonTypeAndMessage(userId, CurrentButtonTypeAndMessage.builder().questionType(questionType)
                .message(question.getQuestion()).build());
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }
}
