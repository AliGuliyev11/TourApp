package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.redis.RedisCache.MessageBoolCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.MessageAndBoolean;
import com.mycode.tourapptelegrambot.redis.RedisCache.ButtonAndMessageCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.redis.RedisCache.QuestionIdAndNextCache;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import org.joda.time.LocalDate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * This class for creating inline keyboard buttons dynamically
 * With the help of QuestionType enum
 */

public class UniversalInlineButtons {


    public SendMessage sendInlineKeyBoardMessage(int userId, long chatId, int messageId, QuestionIdAndNextCache cache,
                                                 Question question, ButtonAndMessageCache buttonAndMessageCache,
                                                 MessageBoolCache messageBoolCache) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(question.getQuestion());
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        Long next = null;
        Long prev = null;
        Long questionId = null;
        QuestionType questionType = null;
        for (var item : question.getQuestionActions()) {
            prev = item.getQuestion().getId();
            next = item.getNext();
            questionId = item.getId();
            questionType = item.getType();
            if (!item.getType().equals(QuestionType.Free_Text) && !item.getType().equals(QuestionType.Button_Calendar) && !item.getType().equals(QuestionType.Button_Numeric)) {
                rowList.add(addInlineKeyboardButton(item.getText(), item.getKeyword(), item.getId()));
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
            } else if (item.getType().equals(QuestionType.Button_Calendar)) {
                cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).prev(prev).next(next).regex(question.getRegex()).build());
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
                return sendMessage.setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now()));
            }
        }

        cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).next(next).regex(question.getRegex()).prev(prev).build());
        buttonAndMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                .message(question.getQuestion()).build());


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    private List<InlineKeyboardButton> addInlineKeyboardButton(String text, String keyword, Long id) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(text);
        keyboardButton.setCallbackData(keyword + id);
        keyboardButtonsRow1.add(keyboardButton);
        return keyboardButtonsRow1;
    }
}
