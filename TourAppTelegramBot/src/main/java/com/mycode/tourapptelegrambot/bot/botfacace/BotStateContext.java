package com.mycode.tourapptelegrambot.bot.botfacace;

import com.mycode.tourapptelegrambot.enums.BotState;
import com.mycode.tourapptelegrambot.enums.QuestionType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

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

    public static boolean isButtonType(QuestionType questionType) {
        switch (questionType) {
            case Button_Calendar:
            case Button_Numeric:
            case Button_Prediction:
            case Button:
            case Button_Free_Text:
                return true;
            default:
                return false;
        }
    }


}





