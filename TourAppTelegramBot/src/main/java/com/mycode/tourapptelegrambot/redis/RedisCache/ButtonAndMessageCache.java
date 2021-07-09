package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ButtonAndMessageCache {
    public static final String HASH_KEY = "CurrentButtonTypeAndMessage";
    private RedisTemplate template;

    public ButtonAndMessageCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(CurrentButtonTypeAndMessage buttonTypeAndMessage){
        if (get(buttonTypeAndMessage.getUserId())!=null){
            delete(buttonTypeAndMessage.getUserId());
        }
        template.opsForHash().put(HASH_KEY,buttonTypeAndMessage.getUserId(),buttonTypeAndMessage);
    }

    public CurrentButtonTypeAndMessage get(int userId){
        return (CurrentButtonTypeAndMessage) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(int userId){
        template.opsForHash().delete(HASH_KEY,userId);
    }
}
