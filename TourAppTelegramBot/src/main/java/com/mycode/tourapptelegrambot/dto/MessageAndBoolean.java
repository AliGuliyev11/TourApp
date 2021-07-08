package com.mycode.tourapptelegrambot.dto;

import lombok.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageAndBoolean {
    SendMessage sendMessage;
    Boolean send;
}
