package com.mycode.tourapptelegrambot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.bot.botfacade.TelegramFacade;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.models.MyUser;
import com.mycode.tourapptelegrambot.models.UserOffer;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;

@SpringBootTest
class TourAppTelegramBotApplicationTests {


    private ModelMapper modelMapper = null;
    private Message message;

    @Autowired
    TelegramFacade telegramFacade;

    @BeforeEach
    void init() {
        message = Mockito.mock(Message.class);
        modelMapper = new ModelMapper();
    }

    /**
     * Agent sends offer and bot convert this offer to UserOffer
     */

    @SneakyThrows
    @Test
    void mapOffer() {

        Offer offer = new Offer();
        offer.setOfferId(1L);
        offer.setUserId("3");
        offer.setFile(new File("src/main/resources/static/docs/image.png"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MyUser myUser = MyUser.builder().id(1l).chatId("2").uuid("13232f").phoneNumber("123354665").build();
        UserOffer converted = mapper.convertValue(offer, UserOffer.class);
        converted.setOfferId(offer.getOfferId());
        converted.setMyUser(myUser);
        converted.setFirstFive(true);

        UserOffer userOffer = new UserOffer();
        userOffer.setOfferId(offer.getOfferId());
        userOffer.setUserId("3");
        userOffer.setFile(new File(offer.getFile().getAbsolutePath()));
        userOffer.setFirstFive(true);
        userOffer.setMyUser(myUser);

        String expected=mapper.writeValueAsString(userOffer);
        String actual=mapper.writeValueAsString(converted);
        System.out.println(expected);
        System.out.println(actual);
        Assertions.assertTrue(expected.equals(actual));
    }

    @SneakyThrows
    @Test
    void getMessageFromContactText() {

        String expected = "+994501234567";

        Message message = new Message();
        message.setMessageId(122);
        Chat chat = new Chat();
        chat.setId(1797927400l);
        message.setChat(chat);
        message.setText("/stop");
        User user = new User();
        user.setId(1797927400l);
        message.setFrom(user);
        Contact contact = new Contact();
        contact.setPhoneNumber("+994501234567");
        message.setContact(contact);
        Method privateMethod = TelegramFacade.class.getDeclaredMethod("messageText", Message.class);
        privateMethod.setAccessible(true);
        String actualText = (String) privateMethod.invoke(telegramFacade, message);
        Assertions.assertEquals(expected, actualText);
    }

    @SneakyThrows
    @Test
    void getMessageText() {

        String expected = "/stop";

        Message message = new Message();
        message.setMessageId(122);
        Chat chat = new Chat();
        chat.setId(1797927400l);
        message.setChat(chat);
        message.setText("/stop");
        User user = new User();
        user.setId(1797927400l);
        message.setFrom(user);
        Method privateMethod = TelegramFacade.class.getDeclaredMethod("messageText", Message.class);
        privateMethod.setAccessible(true);
        String actualText = (String) privateMethod.invoke(telegramFacade, message);
        Assertions.assertEquals(expected, actualText);
    }

    @SneakyThrows
    @Test
    void sendAnswerCallbackQuery() {
        String text = "Test";
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId("4545ryt");

        Method privateMethod = TelegramFacade.class.getDeclaredMethod("sendAnswerCallbackQuery", String.class, CallbackQuery.class);
        privateMethod.setAccessible(true);
        AnswerCallbackQuery sendedMessage = (AnswerCallbackQuery) privateMethod.invoke(telegramFacade, text, callbackQuery);

        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder().text("Test").callbackQueryId("4545ryt").showAlert(true).build();

        Assertions.assertEquals(answerCallbackQuery, sendedMessage);

    }

    @SneakyThrows
    @Test
    void textToLocalDate() {

        String a = "2021-07-14";

        LocalDate localDate = telegramFacade.getLocaleDate(a);
        LocalDate expectedLocalDate = new LocalDate(2021, 7, 14);

        Assertions.assertEquals(expectedLocalDate, localDate);
    }

    @Test
    void getLanguageButtonsTest() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonAz = new InlineKeyboardButton();
        buttonAz.setText("AZ" + Emojis.Azerbaijan);
        InlineKeyboardButton buttonRu = new InlineKeyboardButton();
        buttonRu.setText("RU" + Emojis.Russian);
        InlineKeyboardButton buttonEn = new InlineKeyboardButton();
        buttonEn.setText("EN" + Emojis.English);

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

        Assertions.assertEquals(inlineKeyboardMarkup, getLanguageButtons());

    }



    @Test
    void isPrimitiveTypeTest() {
        Assertions.assertEquals(true, isPrimitive(int.class));
    }

    @Test
    void boxPrimitiveClassTest() {
        Assertions.assertEquals(123, boxPrimitiveClass(int.class, "123"));
    }


}
