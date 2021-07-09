package com.mycode.tourapptelegrambot.redis.redisEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash("MessageAndBoolean")
public class MessageAndBoolean implements Serializable {

    @Id
    private int userId;
//    SendMessage sendMessage;
    Boolean send;
    int MessageId;
}
