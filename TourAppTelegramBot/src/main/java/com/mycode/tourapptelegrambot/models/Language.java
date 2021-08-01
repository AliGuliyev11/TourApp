package com.mycode.tourapptelegrambot.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = "language")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(length = 3)
    String lang;
    String emoji;
    @ManyToMany(targetEntity = Question.class,fetch = FetchType.EAGER)
    Set<Question> questions;
    @ManyToMany(targetEntity = BotMessage.class,fetch = FetchType.EAGER)
    Set<BotMessage> botMessages;
}
