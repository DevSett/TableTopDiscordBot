package ru.devsett.bot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.MessageReceiverService;
import ru.devsett.bot.service.VoiceReceiverService;

@Service
@Log4j2
public class MafiaBot {
    private final GatewayDiscordClient gatewayDiscordClient;
    private final MessageReceiverService messageReceiverService;
    private final VoiceReceiverService voiceReceiverService;

    public MafiaBot(GatewayDiscordClient gatewayDiscordClient,
                    MessageReceiverService messageReceiverService,
                    VoiceReceiverService voiceReceiverService) {
        this.messageReceiverService = messageReceiverService;
        this.gatewayDiscordClient = gatewayDiscordClient;
        this.voiceReceiverService = voiceReceiverService;
    }

    public void init() {
        gatewayDiscordClient.on(MessageCreateEvent.class)
                .subscribe(messageReceiverService::consume);
        gatewayDiscordClient.on(VoiceStateUpdateEvent.class)
                .subscribe(voiceReceiverService::consume);
        gatewayDiscordClient.onDisconnect().block();
    }
}

