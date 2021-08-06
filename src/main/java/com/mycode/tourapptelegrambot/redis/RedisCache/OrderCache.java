package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Ali Guliyev
 * @version 1.0
 * @implNote repository class for cache
 */

@Repository
public class OrderCache {
    public static final String HASH_KEY = "TelegramOrderMapCache";
    private RedisTemplate<String,Object> template;

    public OrderCache(RedisTemplate<String,Object> template) {
        this.template = template;
    }

    public void save(CurrentOrder currentOrder){
        if (get(currentOrder.getUserId())!=null){
            delete(currentOrder.getUserId());
        }
        template.opsForHash().put(HASH_KEY,currentOrder.getUserId(),currentOrder);
    }

    public CurrentOrder get(Long userId){
        CurrentOrder currentOrder=(CurrentOrder) template.opsForHash().get(HASH_KEY,userId);
        if (currentOrder==null){
            return new CurrentOrder();
        }
        return currentOrder;
    }


    public boolean checkOrder(Long userId){
        CurrentOrder currentOrder=(CurrentOrder) template.opsForHash().get(HASH_KEY,userId);
        if (currentOrder==null){
            return false;
        }
        return true;
    }

    public void delete(Long userId){
        if (get(userId)!=null){
            template.opsForHash().delete(HASH_KEY,userId);
        }
    }
}
