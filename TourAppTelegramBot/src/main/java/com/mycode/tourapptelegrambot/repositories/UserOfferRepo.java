package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.UserOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOfferRepo extends JpaRepository<UserOffer, Long> {
    List<UserOffer> getUserOffersByMyUserId(int userId);

    void deleteAllByMyUserId(int userId);
}
