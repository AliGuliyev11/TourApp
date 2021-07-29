package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepo extends JpaRepository<Language,Long> {
}
