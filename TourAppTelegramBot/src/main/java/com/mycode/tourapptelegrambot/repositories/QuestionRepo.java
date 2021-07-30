package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepo extends JpaRepository<Question,Long> {

    @Query(value = "SELECT * from question q WHERE q.is_first=true",nativeQuery = true)
    Question getFirstQuestion();


}
