package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.MessageAndBoolean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MessageBoolCache {
    public static final String HASH_KEY = "MessageCache";
    private RedisTemplate template;

    public MessageBoolCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(MessageAndBoolean messageAndBoolean){
        if (get(messageAndBoolean.getUserId())!=null){
            delete(messageAndBoolean.getUserId());
        }
        template.opsForHash().put(HASH_KEY,messageAndBoolean.getUserId(),messageAndBoolean);
    }

    public MessageAndBoolean get(Long userId){
        return (MessageAndBoolean) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
