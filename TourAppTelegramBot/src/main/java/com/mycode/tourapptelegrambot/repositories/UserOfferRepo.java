package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.UserOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOfferRepo extends JpaRepository<UserOffer, Long> {
    @Query(value = "SELECT * from user_offer u where u.my_user_id=:userId and u.is_first_five=false",nativeQuery = true)
    List<UserOffer> getUserOffersByMyUserId(Long userId);

    @Query(value = "SELECT * from user_offer u where u.my_user_id=:userId",nativeQuery = true)
    List<UserOffer> checkUserOffersByMyUserId(Long userId);

    void deleteAllByMyUserId(Long userId);

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT * FROM user_offer u where u.my_user_id=:userId)THEN true ELSE FALSE END",nativeQuery = true)
    boolean checkUserOffer(Long userId);

}
