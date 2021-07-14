package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CalendarTime;
import com.mycode.tourapptelegrambot.redis.redisEntity.QuestionIdAndNext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CalendarCache {
    public static final String HASH_KEY = "CalendarCache";
    private RedisTemplate template;

    public CalendarCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(CalendarTime calendarTime){
        if (get(calendarTime.getUserId())!=0){
            delete(calendarTime.getUserId());
        }
        template.opsForHash().put(HASH_KEY,calendarTime.getUserId(),calendarTime);
    }

    public Integer get(Long userId){
        CalendarTime time= (CalendarTime) template.opsForHash().get(HASH_KEY,userId);
        if (time==null){
            return 0;
        }
        return time.getTime();
    }


    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
