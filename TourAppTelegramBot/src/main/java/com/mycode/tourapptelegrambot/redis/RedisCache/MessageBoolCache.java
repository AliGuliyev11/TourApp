package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.MessageAndBoolean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MessageBoolCache {
    public static final String HASH_KEY = "MessageAndBoolean";
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

    public MessageAndBoolean get(int userId){
        return (MessageAndBoolean) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(int userId){
        template.opsForHash().delete(HASH_KEY,userId);
    }
}