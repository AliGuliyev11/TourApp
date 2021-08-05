package com.mycode.tourapptelegrambot.inlineButtons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.dto.QuestionAction.QAConverter;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import com.mycode.tourapptelegrambot.redis.RedisCache.MessageBoolCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.MessageAndBoolean;
import com.mycode.tourapptelegrambot.redis.RedisCache.ButtonAndMessageCache;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.redis.RedisCache.QuestionIdAndNextCache;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ali Guliyev
 * @version 1.0
 *
 * @implNote This class for creating inline keyboard buttons dynamically
 * With the help of QuestionType enum
 */

public class UniversalInlineButtons {


    @SneakyThrows
    public SendMessage sendInlineKeyBoardMessage(Long userId, String chatId, int messageId, QuestionIdAndNextCache cache,
                                                 Question question, ButtonAndMessageCache buttonAndMessageCache,
                                                 MessageBoolCache messageBoolCache, BotMessageRepo botMessageRepo, String languages) {
        JSONObject questionText = new JSONObject(question.getQuestion());
        QuestionAction questionAction = question.getQuestionActions();

        JSONObject jsonObject = new JSONObject(questionAction.getText());
        JSONObject convertedQuestionAction = jsonObject.getJSONObject(languages.toUpperCase());
        ObjectMapper objectMapper = new ObjectMapper();
        QAConverter qaConverter = objectMapper.readValue(convertedQuestionAction.toString(), QAConverter.class);
        SendMessage sendMessage = SendMessage.builder().text(questionText.getString(languages.toUpperCase())).chatId(chatId).build();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        Long next = questionAction.getNext();
        Long prev = questionAction.getQuestion().getId();
        Long questionId = questionAction.getId();
        QuestionType questionType = null;

        for (var item : qaConverter.questionAction) {
            questionType = QuestionType.valueOf(item.buttonType);
            if (!item.getButtonType().equals(QuestionType.Free_Text.name()) && !item.getButtonType().equals(QuestionType.Button_Calendar.name())
                    && !item.getButtonType().equals(QuestionType.Button_Numeric.name()) && !item.getButtonType().equals(QuestionType.Button_Keyboard.name())) {
                rowList.add(addInlineKeyboardButton(item.getText(), questionAction.getKeyword(), item.callbackData));
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
            } else if (item.getButtonType().equals(QuestionType.Button_Calendar.name())) {
                cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).prev(prev).next(next).regex(question.getRegex()).build());
                messageBoolCache.save(MessageAndBoolean.builder().userId(userId).send(false).MessageId(messageId).build());
                sendMessage.setReplyMarkup(new CalendarUtil().generateKeyboard(LocalDate.now(), botMessageRepo, languages));
                return sendMessage;
            } else if (item.getButtonType().equals(QuestionType.Button_Keyboard.name())) {
                buttonAndMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                        .message(questionText.getString(languages.toUpperCase())).build());
                cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).prev(prev).next(next).regex(question.getRegex()).build());
                sendMessage.setReplyMarkup(addKeyboardButton(item.getText()));
                return sendMessage;
            }
        }

        cache.save(QuestionIdAndNext.builder().userId(userId).questionId(questionId).next(next).regex(question.getRegex()).prev(prev).build());
        buttonAndMessageCache.save(CurrentButtonTypeAndMessage.builder().userId(userId).questionType(questionType)
                .message(questionText.getString(languages.toUpperCase())).build());


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }


    private ReplyKeyboardMarkup addKeyboardButton(String text) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row2 = new KeyboardRow();
        row2.add(KeyboardButton.builder().text(text + Emojis.Iphone).requestContact(true).build());

        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }


    private List<InlineKeyboardButton> addInlineKeyboardButton(String text, String keyword, String callbackData) {
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton keyboardButton = InlineKeyboardButton.builder().text(text).callbackData(keyword + callbackData).build();
        keyboardButtonsRow1.add(keyboardButton);
        return keyboardButtonsRow1;
    }
}
