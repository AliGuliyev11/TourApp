package com.mycode.tourapptelegrambot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycode.tourapptelegrambot.dto.QuestionAction.QAConverter;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class TestMapper {

    @Test
    @SneakyThrows
    void aaaa()
    {
        String a = "    {\n" +
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
        QAConverter root = om.readValue(b.toString(), QAConverter.class);
        System.out.println(root.questionAction.get(0).text.hashCode());
        System.out.println(root.questionAction.get(0).text.hashCode());
    }
}
