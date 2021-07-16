package com.mycode.tourapptelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/** Telegram bot command
 * @author Ali Guliyev
 * @version 1.0 */

public class StopCommand extends BotCommand {
    public StopCommand() {
        super("stop","Stop\uD83D\uDD1A");
    }
}
