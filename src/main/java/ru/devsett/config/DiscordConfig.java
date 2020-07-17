package ru.devsett.config;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration()
@Getter
public class DiscordConfig {

    @Value("${discord.token}")
    private String token;
    @Value("${discord.prefix}")
    private String prefix;

    @Bean
    public GatewayDiscordClient getGatewayDiscordClient() {
        final String token = getToken();
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        return gateway;
    }
}
