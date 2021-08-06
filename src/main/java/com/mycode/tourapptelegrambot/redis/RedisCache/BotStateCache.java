package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentBotState;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote repository class for cache
 */


@Repository
public class BotStateCache {
    public static final String HASH_KEY = "BotStateCache";
    private RedisTemplate<String,Object> template;

    public BotStateCache(RedisTemplate<String,Object> template) {
        this.template = template;
    }

    public void save(CurrentBotState botState) {
        if (get(botState.getUserId()) != null) {
            delete(botState.getUserId());
        }
        template.opsForHash().put(HASH_KEY, botState.getUserId(), botState);
    }

    public CurrentBotState get(Long userId) {
        return (CurrentBotState) template.opsForHash().get(HASH_KEY, userId);
    }


    public void delete(Long userId) {
        if (get(userId) != null) {
            template.opsForHash().delete(HASH_KEY, userId);
        }
    }
}
