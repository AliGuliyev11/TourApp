package com.mycode.tourapptelegrambot.redis.redisEntity;

import com.mycode.tourapptelegrambot.enums.QuestionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CurrentButtonTypeAndMessageCache")
public class CurrentButtonTypeAndMessage implements Serializable {
    @Id
    private Long userId;
    QuestionType questionType;
    String message;
}
