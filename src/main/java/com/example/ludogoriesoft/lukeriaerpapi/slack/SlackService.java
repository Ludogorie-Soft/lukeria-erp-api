package com.example.ludogoriesoft.lukeriaerpapi.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
public final class SlackService {
    public static String slackBotToken;

    private SlackService() {
    }
    public static void setSlackBotToken(String token) {
        slackBotToken = token;
    }

    public static void publishMessage(String channelName, String message) {
        MethodsClient client = Slack.getInstance().methods();
        try {
             client.chatPostMessage(r -> r
                    .token(slackBotToken)
                    .channel(channelName)
                    .text(message)
            );
        } catch (IOException | SlackApiException e) {
            log.error("Unsuccessful attempt to send a slack notification. Reason: {}", e.getMessage());
        }
    }

}
