package com.mycode.tourapptelegrambot.bot.botfacade;

import com.mycode.tourapptelegrambot.enums.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class for check current bot state
 * @author Ali Guliyev
 * @version 1.0
 * */

@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {

        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);

        return currentMessageHandler.handle(message);
    }

    public InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_TOUR);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case FILLING_TOUR:
                return true;
            default:
                return false;
        }
    }

}





