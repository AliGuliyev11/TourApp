package com.mycode.tourapptelegrambot.controlllers;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

@RestController
public class TourAppController {
    private final TourAppBot telegramBot;

    public TourAppController(TourAppBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        System.out.println(UUID.randomUUID());
        return telegramBot.onWebhookUpdateReceived(update);
//            return new SendMessage(update.getMessage().getChatId(),"Salam");
    }
}
