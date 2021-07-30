package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.Question;
import com.mycode.tourapptelegrambot.models.QuestionAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionActionRepo extends JpaRepository<QuestionAction,Long> {

    QuestionAction findQuestionActionByNext(Long next);

}
