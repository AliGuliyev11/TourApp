package com.mycode.tourapptelegrambot.redis.redisEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote cache entity
 */

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash("MessageCache")
public class MessageAndBoolean implements Serializable {

    @Id
    private Long userId;
    Boolean send;
    int MessageId;
}
