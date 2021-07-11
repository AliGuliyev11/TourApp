package com.mycode.tourapptelegrambot.models;


import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity(name = "question_action")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(targetEntity = Question.class)
    Question question;
    @Enumerated(EnumType.STRING)
    QuestionType type;
    String keyword;
    String text;
    Long next;
}
