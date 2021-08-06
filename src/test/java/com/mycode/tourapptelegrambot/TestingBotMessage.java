package com.mycode.tourapptelegrambot;

import com.google.gson.Gson;
import com.mycode.tourapptelegrambot.models.BotMessage;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.mycode.tourapptelegrambot.inlineButtons.AcceptOffer.getAcceptButtons;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;
import static com.mycode.tourapptelegrambot.utils.Messages.getBotMessage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
public class TestingBotMessage {

    @Autowired
    BotMessageRepo botMessageRepo;


    @Test
    void sendEndingMessage() {
        BotMessage botMessage = new BotMessage();
        Map<String, String> message = new HashMap<>();
        message.put("AZ", "Əla,qısa zamanda sizə təkliflər göndərəcəyik.✅");
        message.put("EN", "Excellent, we will send you suggestions as soon as possible.✅");
        message.put("RU", "Отлично, мы пришлем вам предложения в кратчайшие сроки.✅");
        Gson gson = new Gson();
        String endingMessage = gson.toJson(message);
        botMessage.setMessage(endingMessage);
        botMessage.setKeyword("ending.msg");
        botMessageRepo.save(botMessage);
        String language = "AZ";
        String keyword = "ending.msg";
        String expected = "Əla,qısa zamanda sizə təkliflər göndərəcəyik.✅";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void sendIgnoreMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Yalnız tarixləri seçə bilərsiz ❌");
        messageIgnore.put("EN", "You can only select dates ❌");
        messageIgnore.put("RU", "Вы можете выбрать только даты ❌");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("ignore.message");
        botMessageRepo.save(botMessageIgnore);
        String language = "EN";
        String keyword = "ignore.message";
        String expected = "You can only select dates ❌";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getPrevCalendarMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Yalnız indiki və gələcək zamanı seçə bilərsiz ❌");
        messageIgnore.put("EN", "You can only choose the present and future tenses ❌");
        messageIgnore.put("RU", "Вы можете выбрать только настоящее и будущее время ❌");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("prev.calendar");
        botMessageRepo.save(botMessageIgnore);
        String language = "RU";
        String keyword = "prev.calendar";
        String expected = "Вы можете выбрать только настоящее и будущее время ❌";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getContinueMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Sizə veriləcək sual yoxdur");
        messageIgnore.put("EN", "There is no question for you");
        messageIgnore.put("RU", "Нет вопросов к тебе");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("continue.message");
        botMessageRepo.save(botMessageIgnore);
        String language = "AZ";
        String keyword = "continue.message";
        String expected = "Sizə veriləcək sual yoxdur";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getBotLangMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Botun dili Azərbaycan dili olaraq təyin olundu");
        messageIgnore.put("EN", "Bot's language was designated as English");
        messageIgnore.put("RU", "Язык Ботуна был определен как русский");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("bot.lang");
        botMessageRepo.save(botMessageIgnore);
        String language = "EN";
        String keyword = "bot.lang";
        String expected = "Bot's language was designated as English";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getStartCacheMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Yenidən başlamaq üçün ilk öncə <b> stop </b> yazmalısan\n /stop - prosesi bitirmək üçün\n /continue-suallarınıza davam etmək üçün");
        messageIgnore.put("EN", "To restart, you must first enter <b> /stop </b>\n /stop - end the process\n /continue-to continue your questions");
        messageIgnore.put("RU", "Для перезапуска сначала необходимо ввести <b> /stop </b>\n /stop - завершить процесс\n /continue-чтобы продолжить ваши вопросы");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("start.cache");
        botMessageRepo.save(botMessageIgnore);
        String language = "RU";
        String keyword = "start.cache";
        String expected = "Для перезапуска сначала необходимо ввести <b> /stop </b>\n /stop - завершить процесс\n /continue-чтобы продолжить ваши вопросы";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getDefaultCacheMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Yenidən başlamaq üçün ilk öncə <b> /start </b> yazmalısan\n /stop - prosesi bitirmək üçün");
        messageIgnore.put("EN", "To restart, you must first type <b> /start </b> \n /stop - end the process");
        messageIgnore.put("RU", "Для перезапуска необходимо сначала ввести <b> /start </b>\n/stop - завершить процесс");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("default.cache");
        botMessageRepo.save(botMessageIgnore);
        String language = "AZ";
        String keyword = "default.cache";
        String expected = "Yenidən başlamaq üçün ilk öncə <b> /start </b> yazmalısan\n /stop - prosesi bitirmək üçün";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getAcceptedMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Bu təklifi qəbul elədiz.Sizinlə yaxın zamanda əlaqə saxlanılacaq.");
        messageIgnore.put("EN", "You have accepted this offer. You will be contacted soon.");
        messageIgnore.put("RU", "Вы приняли это предложение. С вами свяжутся в ближайшее время.");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("accepted.message");
        botMessageRepo.save(botMessageIgnore);
        String language = "AZ";
        String keyword = "accepted.message";
        String expected = "Bu təklifi qəbul elədiz.Sizinlə yaxın zamanda əlaqə saxlanılacaq.";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }


    @Test
    void getLoadMoreMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Daha çox yüklə...");
        messageIgnore.put("EN", "Load more...");
        messageIgnore.put("RU", "Загрузи больше...");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("load.more");
        botMessageRepo.save(botMessageIgnore);
        String language = "EN";
        String keyword = "load.more";
        String expected = "Load more...";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }

    @Test
    void getNoLoadMoreMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Hələlik başqa təklif yoxdur.");
        messageIgnore.put("EN", "There is no other offer yet.");
        messageIgnore.put("RU", "Других предложений пока нет.");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("no.more.load");
        botMessageRepo.save(botMessageIgnore);
        String language = "RU";
        String keyword = "no.more.load";
        String expected = "Других предложений пока нет.";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }


    @Test
    void getAcceptOfferMessage() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "Təklifi qəbul et");
        messageIgnore.put("EN", "Accept the offer");
        messageIgnore.put("RU", "Прими предложение");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("accept.offer");
        botMessageRepo.save(botMessageIgnore);
        String language = "AZ";
        String keyword = "accept.offer";
        String expected = "Təklifi qəbul et";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }


    @Test
    @Order(1)
    void getWeekdays() {
        Gson gson = new Gson();
        BotMessage botMessageIgnore = new BotMessage();
        Map<String, String> messageIgnore = new HashMap<>();
        messageIgnore.put("AZ", "B.e,Ç.a,Ç,C.a,C,Ş,B");
        messageIgnore.put("EN", "M,T,W,T,F,ST,S");
        messageIgnore.put("RU", "П,Вт,С,Ч,П,С,В");
        String ignore = gson.toJson(messageIgnore);
        botMessageIgnore.setMessage(ignore);
        botMessageIgnore.setKeyword("weekdays");
        botMessageRepo.save(botMessageIgnore);
        String language = "RU";
        String keyword = "weekdays";
        String expected = "П,Вт,С,Ч,П,С,В";
        Assertions.assertEquals(expected, getBotMessage(keyword, language, botMessageRepo));
    }


    @Test
    void getAcceptButtonsTest() {


        String language = "AZ";
        String keyword = "accept.offer";

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(getBotMessage(keyword, language, botMessageRepo));

        accept.setCallbackData("Offer-123");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        CurrentOrder order= CurrentOrder.builder().languages("AZ").build();
        Assertions.assertEquals(inlineKeyboardMarkup, getAcceptButtons(123l, order, botMessageRepo));
    }

    @Test
    @Order(2)
    void calendarKeyboard() {



        LocalDate date = new LocalDate();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(new SimpleDateFormat("MMM yyyy").format(date.toDate())).build());
        keyboard.add(headerRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : getBotMessage("weekdays", "RU",botMessageRepo).split("[,]")) {
            daysOfWeekRow.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(day).build());
        }
        keyboard.add(daysOfWeekRow);

        LocalDate firstDay = date.dayOfMonth().withMinimumValue();

        int shift = firstDay.dayOfWeek().get() - 1;
        int daysInMonth = firstDay.dayOfMonth().getMaximumValue();
        int rows = ((daysInMonth + shift) % 7 > 0 ? 1 : 0) + (daysInMonth + shift) / 7;
        for (int i = 0; i < rows; i++) {

            List<InlineKeyboardButton> row = new ArrayList<>();
            int day = firstDay.getDayOfMonth();
            LocalDate callbackDate = firstDay;
            for (int j = 0; j < shift; j++) {
                row.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(" ").build());
            }
            for (int j = shift; j < 7; j++) {
                if (day <= (firstDay.dayOfMonth().getMaximumValue())) {

                    if (date.equals(LocalDate.now()) && day==LocalDate.now().getDayOfMonth()) {
                        row.add(InlineKeyboardButton.builder().callbackData(callbackDate.toString()).text("" + Emojis.Clock).build());
                        day++;
                    } else if (firstDay .isBefore(LocalDate.now()) && day < LocalDate.now().getDayOfMonth()) {
                        row.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(" ").build());
                        day++;
                    } else {
                        row.add(InlineKeyboardButton.builder().callbackData(callbackDate.toString()).text(Integer.toString(day++)).build());
                    }
                    callbackDate = callbackDate.plusDays(1);
                } else {
                    row.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(" ").build());
                }

            }
            keyboard.add(row);
            firstDay = firstDay.plusDays(7 - shift);
            shift = 0;
        }

        List<InlineKeyboardButton> controlsRow = new ArrayList<>();
        controlsRow.add(InlineKeyboardButton.builder().callbackData("<").text("<").build());
        controlsRow.add(InlineKeyboardButton.builder().callbackData(">").text(">").build());
        keyboard.add(controlsRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboard);

        Assertions.assertEquals(inlineKeyboardMarkup, new CalendarUtil().generateKeyboard(LocalDate.now(), botMessageRepo, "RU"));
    }

}
