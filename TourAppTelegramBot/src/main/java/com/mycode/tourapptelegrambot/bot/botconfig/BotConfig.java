package com.mycode.tourapptelegrambot.bot.botconfig;

import com.mycode.tourapptelegrambot.bot.TourAppBot;
import com.mycode.tourapptelegrambot.bot.botfacace.TelegramFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatDescription;

@Setter
@Getter
@Configuration
@ConfigurationProperties("telegrambot")
public class BotConfig {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private DefaultBotOptions.ProxyType proxyType;
    private String proxyHost;
    private int proxyPort;


    /** Bot configuration @Bean */

    @Bean
    public TourAppBot tourAppBot(TelegramFacade telegramFacade) {

        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);
        options.setProxyHost(proxyHost);
        options.setProxyPort(proxyPort);
        options.setProxyType(proxyType);

        TourAppBot tourAppBot = new TourAppBot(options,telegramFacade);
        tourAppBot.setBotUsername(botUserName);
        tourAppBot.setBotToken(botToken);
        tourAppBot.setWebhookPath(webHookPath);

        return tourAppBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:application");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
