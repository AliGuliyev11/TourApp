package com.mycode.tourapptelegrambot.redis.redisEntity;

import com.mycode.tourapptelegrambot.enums.BotState;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("BotStateCache")
public class CurrentBotState implements Serializable {
    @Id
    Long userId;
    BotState botState;
}
