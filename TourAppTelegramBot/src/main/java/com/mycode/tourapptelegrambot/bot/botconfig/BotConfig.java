package com.mycode.tourapptelegrambot.bot.botconfig;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.bot.botfacade.TelegramFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


/** This class for telegram bot configuration
 * @author Ali Guliyev
 * @version 1.0
 */

@Setter
@Getter
@Configuration
@ConfigurationProperties("telegrambot")
public class BotConfig {

    private String webHookPath;
    private String botUserName;
    private String botToken;


    /** Bot configuration @Bean */

    @Bean
    public TourAppBot tourAppBot(TelegramFacade telegramFacade) {

        TourAppBot tourAppBot = new TourAppBot(telegramFacade);
        tourAppBot.setBotUsername(botUserName);
        tourAppBot.setBotToken(botToken);
        tourAppBot.setWebhookPath(webHookPath);
        return tourAppBot;
    }



    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
