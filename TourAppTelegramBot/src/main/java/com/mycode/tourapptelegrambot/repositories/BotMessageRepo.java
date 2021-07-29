package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotMessageRepo extends JpaRepository<BotMessage, Long> {
    BotMessage getBotMessageByKeyword(String keyword);
}
