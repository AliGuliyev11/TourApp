package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order,Long> {
}
