package ru.devsett.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.receiver.MasterReceiverService;
import ru.devsett.bot.service.receiver.MessageReceiverService;
import ru.devsett.bot.service.receiver.VoiceReceiverService;

@Service
@Log4j2
public class MafiaBot {
    private final JDA discordClient;
    private final MessageReceiverService messageReceiverService;
    private final MasterReceiverService masterReceiverService;

    private final VoiceReceiverService voiceReceiverService;

    @Getter
    private static Guild guild;

    public MafiaBot(JDA discordClient,
                    MessageReceiverService messageReceiverService,
                    MasterReceiverService masterReceiverService, VoiceReceiverService voiceReceiverService) {
        this.messageReceiverService = messageReceiverService;
        this.discordClient = discordClient;
        this.masterReceiverService = masterReceiverService;
        this.voiceReceiverService = voiceReceiverService;
    }

    @SneakyThrows
    public void init() {
        this.guild = discordClient.getGuilds().stream().findFirst()
                .orElseThrow(() -> new Exception("Don't find guild"));
        discordClient.addEventListener(messageReceiverService, voiceReceiverService);
        masterReceiverService.checkOnBan();

        discordClient.awaitReady();
    }
}

