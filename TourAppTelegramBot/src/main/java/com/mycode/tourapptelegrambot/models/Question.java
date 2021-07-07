package com.mycode.tourapptelegrambot.models;

import com.mycode.tourapptelegrambot.enums.Languages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity(name = "question")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String question;
    boolean isFirst;
    @Enumerated(EnumType.STRING)
    Languages languages;
    @OneToMany(targetEntity = QuestionAction.class, mappedBy = "question")
    List<QuestionAction> questionActions;
}
