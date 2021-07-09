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
@RedisHash("QuestionIdAndNext")
public class QuestionIdAndNext implements Serializable {
    @Id
    private int userId;
    private Long questionId;
    private Long next;
    private String regex;
}