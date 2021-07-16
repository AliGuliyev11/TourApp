package com.mycode.tourapptelegrambot.inlineButtons;

import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.redis.RedisCache.MessageBoolCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.MessageAndBoolean;
import com.mycode.tourapptelegrambot.redis.RedisCache.ButtonAndMessageCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.redis.RedisCache.QuestionIdAndNextCache;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import org.joda.time.LocalDate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * This class for creating inline keyboard buttons dynamically
 * With the help of QuestionType enum
 */

public class UniversalInlineButtons {


    public SendMessage sendInlineKeyBoardMessage(Long userId, String chatId, int messageId, QuestionIdAndNextCache cache,
                                                 Question question, ButtonAndMessageCache buttonAndMessageCache,
                                                 MessageBoolCache messageBoolCache) {
        SendMessage sendMessage = SendMessage.builder().text(question.getQuestion()).chatId(chatId).build();

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
            if (!item.getType().equals(QuestionType.Free_Text) && !item.getType().equals(QuestionType.Button_Calendar)
                    && !item.getType().equals(QuestionType.Button_Numeric) && !item.getType().equals(QuestionType.Button_Keyboard)) {
                rowList.add(addInlineKeyboardButton(item.getText(), item.getKeyword(), item.getId()));
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
            } else if (item.getType().equals(QuestionType.Button_Calendar)) {
                cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).prev(prev).next(next).regex(question.getRegex()).build());
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
                sendMessage.setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now()));
                return sendMessage;
            }else if (item.getType().equals(QuestionType.Button_Keyboard)){
                buttonAndMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                        .message(question.getQuestion()).build());
                cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).prev(prev).next(next).regex(question.getRegex()).build());
                sendMessage.setReplyMarkup(addKeyboardButton(item.getText(),question.getLanguages()));
                return sendMessage;
            }
        }

        cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).next(next).regex(question.getRegex()).prev(prev).build());
        buttonAndMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                .message(question.getQuestion()).build());


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }


    private ReplyKeyboardMarkup addKeyboardButton(String text, Languages languages){
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row2 = new KeyboardRow();
        row2.add(KeyboardButton.builder().text(text+ Emojis.Iphone).requestContact(true).build());

        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }




    private List<InlineKeyboardButton> addInlineKeyboardButton(String text, String keyword, Long id) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = InlineKeyboardButton.builder().text(text).callbackData(keyword+id).build();
        keyboardButtonsRow1.add(keyboardButton);
        return keyboardButtonsRow1;
    }
}
