package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionIdAndNextCache {
    public static final String HASH_KEY = "QuestionNextPrevRegex";
    private RedisTemplate template;

    public QuestionIdAndNextCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(QuestionIdAndNext questionIdAndNext){
        if (get(questionIdAndNext.getUserId())!=null){
            delete(questionIdAndNext.getUserId());
        }
        template.opsForHash().put(HASH_KEY,questionIdAndNext.getUserId(),questionIdAndNext);
    }

    public QuestionIdAndNext get(int userId){
        return (QuestionIdAndNext) template.opsForHash().get(HASH_KEY,userId);
    }


    public void delete(int userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
