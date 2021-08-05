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

@Entity(name = "bot_message")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String keyword;
    @Column(length = 2500)
    String message;
}
