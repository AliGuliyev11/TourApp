package com.mycode.tourapptelegrambot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mycode.tourapptelegrambot.bot.botfacade.TelegramFacade;
import com.mycode.tourapptelegrambot.dto.Offer;
import com.mycode.tourapptelegrambot.models.*;
import com.mycode.tourapptelegrambot.repositories.BotMessageRepo;
import com.mycode.tourapptelegrambot.repositories.LanguageRepo;
import com.mycode.tourapptelegrambot.repositories.QuestionRepo;
import com.mycode.tourapptelegrambot.utils.Emojis;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.boxPrimitiveClass;
import static com.mycode.tourapptelegrambot.checkTypes.TypeCheck.isPrimitive;
import static com.mycode.tourapptelegrambot.inlineButtons.AskLanguage.getLanguageButtons;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class TourAppTelegramBotApplicationTests {


    private ModelMapper modelMapper = null;
    private Message message;

    @Autowired
    TelegramFacade telegramFacade;

    @Autowired
    LanguageRepo languageRepo;

    @Autowired
    QuestionRepo questionRepo;

    @Autowired
    BotMessageRepo botMessageRepo;


    @BeforeEach
    void init() {
        message = Mockito.mock(Message.class);
        modelMapper = new ModelMapper();
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
    @Order(1)
    void getLanguageButtonsNullTest() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        LinkedList<Language> languages = telegramFacade.languageChecker();
        Assertions.assertEquals(inlineKeyboardMarkup, getLanguageButtons(languages));

    }

//    @SneakyThrows
//    @Test
//    @Order(2)
//    void addBotMessage() {
//        BotMessage botMessage = new BotMessage();
//        Map<String, String> message = new HashMap<>();
//        message.put("AZ", "Əla,qısa zamanda sizə təkliflər göndərəcəyik.✅");
//        message.put("EN", "Excellent, we will send you suggestions as soon as possible.✅");
//        message.put("RU", "Отлично, мы пришлем вам предложения в кратчайшие сроки.✅");
//        Gson gson = new Gson();
//        String endingMessage = gson.toJson(message);
//        botMessage.setMessage(endingMessage);
//        botMessage.setKeyword("ending.msg");
//        botMessageRepo.save(botMessage);
//    }

    @SneakyThrows
    @Test
    @Order(3)
    void addQuestion() {
        Question question = new Question();
        question.setQuestion("{'AZ':'Səyahət tipini seçin\uD83E\uDDF3','EN':'Select the type of travel\uD83E\uDDF3','RU':'Выберите тип путешествия\uD83E\uDDF3'}");
        question.setFirst(false);
        question.setRegex(".*");
        questionRepo.save(question);
    }


    @SneakyThrows
    @Test
    @Order(4)
    void addLanguage() {

        Language language = new Language();
        language.setLang("AZ");
        language.setEmoji(Emojis.Azerbaijan.toString());
        Set<BotMessage> botMessageSet = new HashSet<>(botMessageRepo.findAll());
        Set<Question> questions = new HashSet<>(questionRepo.findAll());
        language.setBotMessages(botMessageSet);
        language.setQuestions(questions);
        languageRepo.save(language);
        Assertions.assertEquals(language.getQuestions().isEmpty(), languageRepo.findById(1l).get().getQuestions().isEmpty());
    }

    @Test
    @Order(5)
    void getLanguageButtonsNotNullTest() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonAz = new InlineKeyboardButton();
        buttonAz.setText("AZ" + Emojis.Azerbaijan);

        buttonAz.setCallbackData("LangButtonAZ");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonAz);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);


        inlineKeyboardMarkup.setKeyboard(rowList);
        LinkedList<Language> languages = telegramFacade.languageChecker();
        System.out.println(languages);
        Assertions.assertEquals(inlineKeyboardMarkup, getLanguageButtons(languages));

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
