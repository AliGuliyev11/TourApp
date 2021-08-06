package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote repository class for cache
 */

@Repository
public class QuestionIdAndNextCache {
    public static final String HASH_KEY = "QuestionCache";
    private RedisTemplate<String,Object> template;

    public QuestionIdAndNextCache(RedisTemplate<String,Object> template) {
        this.template = template;
    }

    public void save(QuestionIdAndNext questionIdAndNext){
        if (get(questionIdAndNext.getUserId())!=null){
            delete(questionIdAndNext.getUserId());
        }
        template.opsForHash().put(HASH_KEY,questionIdAndNext.getUserId(),questionIdAndNext);
    }

    public QuestionIdAndNext get(Long userId){
        return (QuestionIdAndNext) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
