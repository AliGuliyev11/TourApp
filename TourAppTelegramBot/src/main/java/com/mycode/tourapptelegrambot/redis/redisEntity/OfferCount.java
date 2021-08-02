package com.mycode.tourapptelegrambot.redis.redisEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote cache entity
 */

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("OfferCache")
public class OfferCount implements Serializable {
    @Id
    Long userId;
    Integer count;
}
