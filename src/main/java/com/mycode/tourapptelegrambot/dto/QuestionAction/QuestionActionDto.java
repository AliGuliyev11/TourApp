package com.mycode.tourapptelegrambot.dto.QuestionAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** @author Ali Guliyev
 * @version 1.0
 * @implNote  This DTO for json converter from DB,see also QAConverter DTO*/


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

