package ru.devsett.bot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.receiver.MasterReceiverService;
import ru.devsett.bot.service.receiver.MessageReceiverService;
import ru.devsett.bot.service.receiver.VoiceReceiverService;

@Service
@Log4j2
public class MafiaBot {
    private final GatewayDiscordClient gatewayDiscordClient;
    private final MessageReceiverService messageReceiverService;
    private final MasterReceiverService masterReceiverService;

    private final VoiceReceiverService voiceReceiverService;

    @Getter
    private static Guild guild;

    public MafiaBot(GatewayDiscordClient gatewayDiscordClient,
                    MessageReceiverService messageReceiverService,
                    MasterReceiverService masterReceiverService, VoiceReceiverService voiceReceiverService) {
        this.messageReceiverService = messageReceiverService;
        this.gatewayDiscordClient = gatewayDiscordClient;
        this.masterReceiverService = masterReceiverService;
        this.voiceReceiverService = voiceReceiverService;
    }

    public void init() {
        this.guild = gatewayDiscordClient.getGuilds().blockFirst();

        gatewayDiscordClient.on(MessageCreateEvent.class)
                .subscribe(messageReceiverService::consume);
        gatewayDiscordClient.on(VoiceStateUpdateEvent.class)
                .subscribe(voiceReceiverService::consume);

        masterReceiverService.checkOnBan();

        gatewayDiscordClient.onDisconnect().block();
    }
}

