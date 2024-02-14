package com.example.ludogoriesoft.lukeriaerpapi.slack;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {
    @Value("${slack.bot.token}")
    private String slackBotToken;

    @PostConstruct
    private void injectSlackBotTokenInStaticField() {
        SlackService.setSlackBotToken(slackBotToken);
    }
}
