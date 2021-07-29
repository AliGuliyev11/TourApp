package com.mycode.tourapptelegrambot.redis.redisEntity;

import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.models.Order;
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
@RedisHash("TelegramMapCache")
public class CurrentOrder implements Serializable {
    @Id
    Long userId;
    Languages languages;
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
