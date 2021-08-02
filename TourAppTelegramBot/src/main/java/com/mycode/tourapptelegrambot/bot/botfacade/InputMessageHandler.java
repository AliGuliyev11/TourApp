package com.mycode.tourapptelegrambot.bot.botfacade;

import com.mycode.tourapptelegrambot.enums.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/** @author Ali Guliyev
 * @version 1.0 */

public interface InputMessageHandler {
    SendMessage handle(Message message);

    BotState getHandlerName();
}
