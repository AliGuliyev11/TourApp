package com.mycode.tourapptelegrambot.redis.redisEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("TelegramOrderMapCache")
public class CurrentOrder implements Serializable {
    @Id
    Long userId;
    String languages;
    Map<String,String> order;

    @Override
    public String toString() {
        return "CurrentOrder{" +
                "userId=" + userId +
                ", languages=" + languages +
                ", order=" + order +
                '}';
    }
}
