package com.mycode.tourapptelegrambot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.dto.QuestionAction.QAConverter;
import com.mycode.tourapptelegrambot.dto.QuestionAction.QuestionActionDto;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestMapper {

    @Test
    @SneakyThrows
    void stringWithList() {
        String a = "{\n" +
                "        \"AZ\": {\n" +
                "            \"QuestionAction\": [\n" +
                "                {\n" +
                "                    \"Text\": \"Kontaktımdan götür\",\n" +
                "                    \"ButtonType\": \"Button_Keyboard\",\n" +
                "                    \"CallbackData\":\"1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"EN\": {\n" +
                "            \"QuestionAction\": [\n" +
                "                {\n" +
                "                    \"Text\": \"Take it from my contact\",\n" +
                "                    \"ButtonType\": \"Button_Keyboard\",\n" +
                "                    \"CallbackData\":\"1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"RU\": {\n" +
                "            \"QuestionAction\": [\n" +
                "                {\n" +
                "                    \"Text\": \"Возьми это у моего контакта\",\n" +
                "                    \"ButtonType\": \"Button_Keyboard\",\n" +
                "                    \"CallbackData\":\"1\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }";
        JSONObject jsonObject = new JSONObject(a);
        JSONObject b = jsonObject.getJSONObject("RU");
        ObjectMapper om = new ObjectMapper();
        QAConverter qaConverter = om.readValue(b.toString(), QAConverter.class);

        List<QuestionActionDto> questionActionList = new ArrayList<>();

        QuestionActionDto questionActionDto = new QuestionActionDto();
        questionActionDto.callbackData = "1";
        questionActionDto.text = "Возьми это у моего контакта";
        questionActionDto.buttonType = "Button_Keyboard";
        questionActionList.add(questionActionDto);
        Assertions.assertEquals(questionActionList, qaConverter.questionAction);
    }

    @Test
    @SneakyThrows
    void stringWithoutList() {
        String a = "{\n" +
                "    \"AZ\": \"Sizə veriləcək sual yoxdur\",\n" +
                "    \"EN\": \"There is no question for you\",\n" +
                "    \"RU\": \"Нет вопросов к тебе\"\n" +
                "}";
        JSONObject jsonObject = new JSONObject(a);
        String text = jsonObject.getString("AZ");
        String expected = "Sizə veriləcək sual yoxdur";
        Assertions.assertEquals(expected, text);
    }


}
