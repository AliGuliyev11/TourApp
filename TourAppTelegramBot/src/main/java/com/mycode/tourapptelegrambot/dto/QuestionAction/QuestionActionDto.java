package com.mycode.tourapptelegrambot.dto.QuestionAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class QuestionActionDto {
    @JsonProperty("Text")
    public String text;
    @JsonProperty("ButtonType")
    public String buttonType;
    @JsonProperty("CallbackData")
    public String callbackData;

}

