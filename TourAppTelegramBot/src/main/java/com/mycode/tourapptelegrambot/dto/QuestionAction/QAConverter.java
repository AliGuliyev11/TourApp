package com.mycode.tourapptelegrambot.dto.QuestionAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QAConverter {
        @JsonProperty("QuestionAction")
        public List<QuestionActionDto> questionAction;
}