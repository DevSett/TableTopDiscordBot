package ru.devsett.bot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.devsett.bot.service.ReceiverService;
import ru.devsett.bot.service.TelegramService;
import ru.devsett.config.TelegramConfig;

@Service
@Log4j2
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

