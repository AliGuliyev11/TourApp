package com.mycode.tourapptelegrambot.redis.RedisCache;

import com.mycode.tourapptelegrambot.models.Order;
import com.mycode.tourapptelegrambot.redis.redisEntity.CurrentOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCache {
    public static final String HASH_KEY = "CurrentOrder";
    private RedisTemplate template;

    public OrderCache(RedisTemplate template) {
        this.template = template;
    }

    public void save(CurrentOrder currentOrder){
        if (get(currentOrder.getUserId())!=null){
            delete(currentOrder.getUserId());
        }
        template.opsForHash().put(HASH_KEY,currentOrder.getUserId(),currentOrder);
    }

    public Order get(int userId){
        CurrentOrder currentOrder=(CurrentOrder) template.opsForHash().get(HASH_KEY,userId);
        if (currentOrder==null){
            return new Order();
        }
        return currentOrder.getOrder();
    }


    public void delete(int userId){
        template.opsForHash().delete(HASH_KEY,userId);
    }
}
