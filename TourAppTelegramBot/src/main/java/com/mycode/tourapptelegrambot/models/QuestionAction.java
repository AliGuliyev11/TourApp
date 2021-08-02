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

@Entity(name = "question_action")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(targetEntity = Question.class)
    Question question;
    String keyword;
    @Column(length = 2500)
    String text;
    Long next;
}
