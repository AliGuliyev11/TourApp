package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

public interface BotMessageRepo extends JpaRepository<BotMessage, Long> {
    BotMessage getBotMessageByKeyword(String keyword);
}
