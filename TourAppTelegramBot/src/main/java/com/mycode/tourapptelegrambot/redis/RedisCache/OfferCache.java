package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.OfferCount;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote repository class for cache
 */

@Repository
public class OfferCache {
    public static final String HASH_KEY = "OfferCache";
    private RedisTemplate template;

    public OfferCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(OfferCount offerCount){
        if (get(offerCount.getUserId())!=0){
            delete(offerCount.getUserId());
        }
        template.opsForHash().put(HASH_KEY,offerCount.getUserId(),offerCount);
    }

    public Integer get(Long userId){
        OfferCount count= (OfferCount) template.opsForHash().get(HASH_KEY,userId);
        if (count==null){
            return 0;
        }
        return count.getCount();
    }


    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
