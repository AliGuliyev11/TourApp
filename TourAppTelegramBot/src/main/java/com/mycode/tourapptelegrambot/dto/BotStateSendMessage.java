package com.mycode.tourapptelegrambot.dto;

import com.mycode.tourapptelegrambot.enums.BotState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Getter
@Setter
@Builder(toBuilder = true)
public class BotStateSendMessage {
    BotState botState;
    SendMessage sendMessage;
}
