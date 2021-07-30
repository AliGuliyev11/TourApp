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
        String a = "{\n" +
                "    \"AZ\": {\n" +
                "        \"QuestionAction\": [\n" +
                "            {\n" +
                "                \"Text\": \"İstirahət-gəzinti\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Ekskursiya\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Ekstremal\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Fərq etməz\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"EN\": {\n" +
                "        \"QuestionAction\": [\n" +
                "            {\n" +
                "                \"Text\": \"Recreation and walking\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Excursion\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Extreme\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Does not matter\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"RU\": {\n" +
                "        \"QuestionAction\": [\n" +
                "            {\n" +
                "                \"Text\": \"Отдых и прогулки\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Экскурсия\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Экстремальный\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"Text\": \"Это не важно\",\n" +
                "                \"ButtonType\": \"Button\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JSONObject jsonObject = new JSONObject(a);
        JSONObject b = jsonObject.getJSONObject("AZ");
        ObjectMapper om = new ObjectMapper();
        QAConverter root = om.readValue(b.toString(), QAConverter.class);
        System.out.println(root.questionAction.size());
    }
}
