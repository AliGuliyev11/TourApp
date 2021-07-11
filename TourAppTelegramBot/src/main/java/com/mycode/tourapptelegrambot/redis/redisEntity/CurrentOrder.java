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
@RedisHash("Order")
public class CurrentOrder implements Serializable {
    @Id
    int userId;
    Order order;
}
