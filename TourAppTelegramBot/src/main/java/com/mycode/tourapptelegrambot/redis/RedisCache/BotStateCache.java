package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentBotState;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BotStateCache {
    public static final String HASH_KEY = "CurrentBotState";
    private RedisTemplate template;

    public BotStateCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(CurrentBotState botState) {
        if (get(botState.getUserId()) != null) {
            delete(botState.getUserId());
        }
        template.opsForHash().put(HASH_KEY, botState.getUserId(), botState);
    }

    public CurrentBotState get(int userId) {
        return (CurrentBotState) template.opsForHash().get(HASH_KEY, userId);
    }


    public void delete(int userId) {
        if (get(userId) != null) {
            template.opsForHash().delete(HASH_KEY, userId);
        }
    }
}
