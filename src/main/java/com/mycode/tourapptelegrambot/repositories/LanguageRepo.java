package com.mycode.tourapptelegrambot.repositories;

import com.mycode.tourapptelegrambot.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ali Guliyev
 * @version 1.0
 */

public interface LanguageRepo extends JpaRepository<Language,Long> {
}
