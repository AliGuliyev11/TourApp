package com.mycode.tourapptelegrambot.redis.redisEntity;

import com.mycode.tourapptelegrambot.models.Order;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("TelegramOrderCache")
public class CurrentOrder implements Serializable {
    @Id
    Long userId;
    Order order;
}
