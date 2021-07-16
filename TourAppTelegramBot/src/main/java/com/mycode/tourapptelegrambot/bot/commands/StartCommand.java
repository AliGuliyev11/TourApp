package com.mycode.tourapptelegrambot.bot.commands;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

/** Telegram bot command
 * @author Ali Guliyev
 * @version 1.0 */

public class StartCommand extends BotCommand {

    public StartCommand() {
        super("start", "Start\uD83D\uDD90\uFE0F");
    }
}
