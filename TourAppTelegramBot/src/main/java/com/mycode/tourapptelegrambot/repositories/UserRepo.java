package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<MyUser,Integer> {

}
