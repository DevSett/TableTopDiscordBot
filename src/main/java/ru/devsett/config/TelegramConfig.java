package ru.devsett.config;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration()
@Getter
public class TelegramConfig {
    @Value("${telegram.token}")
    private String token;
    @Value("${telegram.username}")
    private String username;
}
