package com.mycode.tourapptelegrambot.dto;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIdAndNext {
    private Long questionId;
    private Long next;
    private String regex;
}
