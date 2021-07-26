package com.mycode.tourapptelegrambot;

import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.services.LocaleMessageService;
import com.mycode.tourapptelegrambot.utils.CalendarUtil;
import com.mycode.tourapptelegrambot.utils.Emojis;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mycode.tourapptelegrambot.inlineButtons.AcceptOffer.getAcceptButtons;
import static com.mycode.tourapptelegrambot.utils.CalendarUtil.IGNORE;

@SpringBootTest
public class TestingLocale {

    @Autowired
    LocaleMessageService messageService;

    @Test
    void sendEndingMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Əla,qısa zamanda sizə təkliflər göndərəcəyik." + Emojis.SUCCESS_MARK,
                messageService.getMessage("ending.msg", order.getLanguage(), Emojis.SUCCESS_MARK));
    }

    @Test
    void sendEndingMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Отлично, мы пришлем вам предложения в кратчайшие сроки." + Emojis.SUCCESS_MARK,
                messageService.getMessage("ending.msg", order.getLanguage(), Emojis.SUCCESS_MARK));
    }

    @Test
    void sendEndingMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("Excellent, we will send you suggestions as soon as possible." + Emojis.SUCCESS_MARK,
                messageService.getMessage("ending.msg", order.getLanguage(), Emojis.SUCCESS_MARK));
    }

    @Test
    void sendIgnoreMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Yalnız tarixləri seçə bilərsiz" + Emojis.Times,
                messageService.getMessage("ignore.message", order.getLanguage(), Emojis.Times));
    }

    @Test
    void sendIgnoreMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("You can only select dates" + Emojis.Times,
                messageService.getMessage("ignore.message", order.getLanguage(), Emojis.Times));
    }

    @Test
    void sendIgnoreMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Вы можете выбрать только даты" + Emojis.Times,
                messageService.getMessage("ignore.message", order.getLanguage(), Emojis.Times));
    }

    @Test
    void getPrevCalendarMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Yalnız indiki və gələcək zamanı seçə bilərsiz" + Emojis.Times,
                messageService.getMessage("prev.calendar", order.getLanguage(), Emojis.Times));
    }

    @Test
    void getPrevCalendarMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("You can only choose the present and future tenses" + Emojis.Times,
                messageService.getMessage("prev.calendar", order.getLanguage(), Emojis.Times));
    }

    @Test
    void getPrevCalendarMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Вы можете выбрать только настоящее и будущее время" + Emojis.Times,
                messageService.getMessage("prev.calendar", order.getLanguage(), Emojis.Times));
    }

    @Test
    void getContinueMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Sizə veriləcək sual yoxdur",
                messageService.getMessage("continue.message", order.getLanguage()));
    }

    @Test
    void getContinueMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("There is no question for you",
                messageService.getMessage("continue.message", order.getLanguage()));
    }

    @Test
    void getContinueMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Нет вопросов к тебе",
                messageService.getMessage("continue.message", order.getLanguage()));
    }

    @Test
    void getBotLangMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Botun dili Azərbaycan dili olaraq təyin olundu",
                messageService.getMessage("bot.lang", order.getLanguage()));
    }

    @Test
    void getBotLangMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("Bot's language was designated as English",
                messageService.getMessage("bot.lang", order.getLanguage()));
    }

    @Test
    void getBotLangMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Язык Ботуна был определен как русский",
                messageService.getMessage("bot.lang", order.getLanguage()));
    }

    @Test
    void getStartCacheMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Yenidən başlamaq üçün ilk öncə <b> stop </b> yazmalısan\n /stop - prosesi bitirmək üçün" +
                        "\n /continue-suallarınıza davam etmək üçün",
                messageService.getMessage("start.cache", order.getLanguage()));
    }

    @Test
    void getStartCacheMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("To restart, you must first enter <b> /stop </b>\n /stop - end the process" +
                        "\n /continue-to continue your questions",
                messageService.getMessage("start.cache", order.getLanguage()));
    }

    @Test
    void getStartCacheMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Для перезапуска сначала необходимо ввести <b> /stop </b>\n /stop - завершить процесс" +
                        "\n /continue-чтобы продолжить ваши вопросы",
                messageService.getMessage("start.cache", order.getLanguage()));
    }

    @Test
    void getDefaultCacheMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Yenidən başlamaq üçün ilk öncə <b> /start </b> yazmalısan\n" +
                        " /stop - prosesi bitirmək üçün ",
                messageService.getMessage("default.cache", order.getLanguage()));
    }

    @Test
    void getDefaultCacheMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("To restart, you must first type <b> /start </b> \n" +
                        " /stop - end the process",
                messageService.getMessage("default.cache", order.getLanguage()));
    }

    @Test
    void getDefaultCacheMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Для перезапуска необходимо сначала ввести <b> /start </b>\n" +
                        "/stop - завершить процесс",
                messageService.getMessage("default.cache", order.getLanguage()));
    }

    @Test
    void getAcceptedMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Bu təklifi qəbul elədiz.Sizinlə yaxın zamanda əlaqə saxlanılacaq.",
                messageService.getMessage("accepted.message", order.getLanguage()));
    }

    @Test
    void getAcceptedMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("You have accepted this offer. You will be contacted soon.",
                messageService.getMessage("accepted.message", order.getLanguage()));
    }

    @Test
    void getAcceptedMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Вы приняли это предложение. С вами свяжутся в ближайшее время.",
                messageService.getMessage("accepted.message", order.getLanguage()));
    }

    @Test
    void getLoadMoreMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Daha çox yüklə...",
                messageService.getMessage("load.more", order.getLanguage()));
    }

    @Test
    void getLoadMoreMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("Load more...",
                messageService.getMessage("load.more", order.getLanguage()));
    }

    @Test
    void getLoadMoreMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Загрузи больше...",
                messageService.getMessage("load.more", order.getLanguage()));
    }

    @Test
    void getNoLoadMoreMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Hələlik başqa təklif yoxdur.",
                messageService.getMessage("no.more.load", order.getLanguage()));
    }

    @Test
    void getNoLoadMoreMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("There is no other offer yet.",
                messageService.getMessage("no.more.load", order.getLanguage()));
    }

    @Test
    void getNoLoadMoreMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Других предложений пока нет.",
                messageService.getMessage("no.more.load", order.getLanguage()));
    }

    @Test
    void getAcceptOfferMessageAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        Assertions.assertEquals("Təklifi qəbul et",
                messageService.getMessage("accept.offer", order.getLanguage()));
    }

    @Test
    void getAcceptOfferMessageEn() {
        Order order = Order.builder().language(Languages.EN).build();
        Assertions.assertEquals("Accept the offer",
                messageService.getMessage("accept.offer", order.getLanguage()));
    }

    @Test
    void getAcceptOfferMessageRu() {
        Order order = Order.builder().language(Languages.RU).build();
        Assertions.assertEquals("Прими предложение",
                messageService.getMessage("accept.offer", order.getLanguage()));
    }

    @Test
    void getWeekdaysAz() {
        Order order = Order.builder().language(Languages.AZ).build();
        String[] expected = {"B.e", "Ç.a", "Ç", "C.a", "C", "Ş", "B"};
        Assertions.assertEquals(Arrays.toString(expected),
                Arrays.toString(messageService.getMessage("weekdays", order.getLanguage()).split("[,]")));
    }

    @Test
    void getWeekdaysEn() {
        Order order = Order.builder().language(Languages.EN).build();
        String[] expected = {"M", "T", "W", "T", "F", "ST", "S"};
        Assertions.assertEquals(Arrays.toString(expected),
                Arrays.toString(messageService.getMessage("weekdays", order.getLanguage()).split("[,]")));
    }

    @Test
    void getWeekdaysRu() {
        Order order = Order.builder().language(Languages.RU).build();

        String[] expected = {"П", "Вт", "С", "Ч", "П", "С", "В"};

        Assertions.assertEquals(Arrays.toString(expected),
                Arrays.toString(messageService.getMessage("weekdays", order.getLanguage()).split("[,]")));
    }

    @Test
    void getAcceptButtonsAzTest() {

        Order order = Order.builder().language(Languages.AZ).build();


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(messageService.getMessage("accept.offer", Languages.AZ));

        accept.setCallbackData("Offer-123");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        Assertions.assertEquals(inlineKeyboardMarkup, getAcceptButtons(123l, order, messageService));
    }

    @Test
    void getAcceptButtonsRuTest() {

        Order order = Order.builder().language(Languages.RU).build();


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(messageService.getMessage("accept.offer", Languages.RU));

        accept.setCallbackData("Offer-123");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        Assertions.assertEquals(inlineKeyboardMarkup, getAcceptButtons(123l, order, messageService));
    }

    @Test
    void getAcceptButtonsEnTest() {

        Order order = Order.builder().language(Languages.EN).build();


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton accept = new InlineKeyboardButton();
        accept.setText(messageService.getMessage("accept.offer", Languages.EN));

        accept.setCallbackData("Offer-123");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(accept);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        Assertions.assertEquals(inlineKeyboardMarkup, getAcceptButtons(123l, order, messageService));
    }


    @Test
    void calendarKeyboardAzTest() {

        LocalDate date = new LocalDate();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(new SimpleDateFormat("MMM yyyy").format(date.toDate())).build());
        keyboard.add(headerRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : messageService.getMessage("weekdays", Languages.AZ).split("[,]")) {
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

                    row.add(InlineKeyboardButton.builder().callbackData(callbackDate.toString()).text(Integer.toString(day++)).build());
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

        Assertions.assertEquals(inlineKeyboardMarkup, new CalendarUtil().generateKeyboard(LocalDate.now(), messageService, Languages.AZ));
    }

    @Test
    void calendarKeyboardEnTest() {

        LocalDate date = new LocalDate();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(new SimpleDateFormat("MMM yyyy").format(date.toDate())).build());
        keyboard.add(headerRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : messageService.getMessage("weekdays", Languages.EN).split("[,]")) {
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

                    row.add(InlineKeyboardButton.builder().callbackData(callbackDate.toString()).text(Integer.toString(day++)).build());
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

        Assertions.assertEquals(inlineKeyboardMarkup, new CalendarUtil().generateKeyboard(LocalDate.now(), messageService, Languages.EN));
    }

    @Test
    void calendarKeyboardRuTest() {

        LocalDate date = new LocalDate();

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> headerRow = new ArrayList<>();
        headerRow.add(InlineKeyboardButton.builder().callbackData(IGNORE).text(new SimpleDateFormat("MMM yyyy").format(date.toDate())).build());
        keyboard.add(headerRow);

        List<InlineKeyboardButton> daysOfWeekRow = new ArrayList<>();
        for (String day : messageService.getMessage("weekdays", Languages.RU).split("[,]")) {
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

                    row.add(InlineKeyboardButton.builder().callbackData(callbackDate.toString()).text(Integer.toString(day++)).build());
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

        Assertions.assertEquals(inlineKeyboardMarkup, new CalendarUtil().generateKeyboard(LocalDate.now(), messageService, Languages.RU));
    }
}
