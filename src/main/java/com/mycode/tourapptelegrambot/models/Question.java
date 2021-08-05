package com.mycode.tourapptelegrambot.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

@Entity(name = "question")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(length = 2500)
    String question;
    boolean isFirst;
    String regex;
    @OneToOne(targetEntity = QuestionAction.class)
    QuestionAction questionActions;
}
