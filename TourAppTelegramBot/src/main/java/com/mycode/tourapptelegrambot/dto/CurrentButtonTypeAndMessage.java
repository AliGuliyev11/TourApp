package com.mycode.tourapptelegrambot.dto;

import com.mycode.tourapptelegrambot.enums.QuestionType;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CurrentButtonTypeAndMessage {
    QuestionType questionType;
    String message;
}
