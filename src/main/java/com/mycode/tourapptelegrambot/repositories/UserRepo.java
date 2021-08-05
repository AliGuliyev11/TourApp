package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

public interface UserRepo extends JpaRepository<MyUser, Long> {
    MyUser getMyUserByUuid(String uuid);
}
