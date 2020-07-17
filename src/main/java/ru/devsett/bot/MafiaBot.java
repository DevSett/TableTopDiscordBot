package ru.devsett.bot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.ReceiverService;
import ru.devsett.config.DiscordConfig;

@Service
public class MafiaBot {
    private final GatewayDiscordClient gatewayDiscordClient;
    private final ReceiverService receiverService;

    public MafiaBot(GatewayDiscordClient gatewayDiscordClient, ReceiverService receiverService) {
        this.receiverService = receiverService;
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    public void init() {
        gatewayDiscordClient.on(MessageCreateEvent.class).subscribe(receiverService::consume);
        gatewayDiscordClient.onDisconnect().block();
    }
}

