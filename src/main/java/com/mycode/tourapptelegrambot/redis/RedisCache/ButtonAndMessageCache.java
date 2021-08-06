package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentButtonTypeAndMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote repository class for cache
 */

@Repository
public class ButtonAndMessageCache {
    public static final String HASH_KEY = "CurrentButtonTypeAndMessageCache";
    private RedisTemplate<String,Object> template;

    public ButtonAndMessageCache(RedisTemplate<String,Object> template) {
        this.template = template;
    }

    public void save(CurrentButtonTypeAndMessage buttonTypeAndMessage){
        if (get(buttonTypeAndMessage.getUserId())!=null){
            delete(buttonTypeAndMessage.getUserId());
        }
        template.opsForHash().put(HASH_KEY,buttonTypeAndMessage.getUserId(),buttonTypeAndMessage);
    }

    public CurrentButtonTypeAndMessage get(Long userId){
        return (CurrentButtonTypeAndMessage) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
