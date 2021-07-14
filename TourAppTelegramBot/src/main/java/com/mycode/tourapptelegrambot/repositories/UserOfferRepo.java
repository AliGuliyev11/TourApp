package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.UserOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOfferRepo extends JpaRepository<UserOffer, Long> {
    @Query(value = "SELECT * from user_offer u where u.my_user_id=:userId and u.is_first_five=false",nativeQuery = true)
    List<UserOffer> getUserOffersByMyUserId(Long userId);

    void deleteAllByMyUserId(Long userId);
}
