package com.mycode.tourapptelegrambot.dao;

import com.mycode.tourapptelegrambot.enums.Languages;
import com.mycode.tourapptelegrambot.models.QuestionAction;

import java.util.List;

public interface IQuestionActionDAO {
    List<QuestionAction> getAllQuestionActions();
    List<QuestionAction> getAllQuestionByLanguage(Languages languages);
}
