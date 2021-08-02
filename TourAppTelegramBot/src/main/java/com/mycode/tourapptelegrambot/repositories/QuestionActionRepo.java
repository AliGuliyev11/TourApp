package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.QuestionAction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

public interface QuestionActionRepo extends JpaRepository<QuestionAction,Long> {

    QuestionAction findQuestionActionByNext(Long next);

}
