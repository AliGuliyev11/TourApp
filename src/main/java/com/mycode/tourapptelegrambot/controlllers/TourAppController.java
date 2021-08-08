package com.mycode.tourapptelegrambot.controlllers;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.repositories.UserRepo;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

@RestController
public class TourAppController {

    private final TourAppBot telegramBot;
    UserRepo userRepo;

    public TourAppController(TourAppBot telegramBot, UserRepo userRepo) {
        this.telegramBot = telegramBot;
        this.userRepo = userRepo;
    }

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
