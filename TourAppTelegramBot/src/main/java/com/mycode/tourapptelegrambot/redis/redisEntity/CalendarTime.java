package com.mycode.tourapptelegrambot.redis.redisEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CalendarTime")
public class CalendarTime implements Serializable {
    @Id
    int userId;
    Integer time;
}
