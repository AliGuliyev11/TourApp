package com.mycode.tourapptelegrambot.dto.QuestionAction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote This DTO for convert json to this DTO
 */

@Getter
@Setter
public class QAConverter {
    @JsonProperty("QuestionAction")
    public List<QuestionActionDto> questionAction;
}
