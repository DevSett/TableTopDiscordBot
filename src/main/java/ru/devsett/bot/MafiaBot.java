package ru.devsett.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.receiver.*;

@Service
@Log4j2
public class MafiaBot {
    private final JDA discordClient;
    private final MessageReceiverService messageReceiverService;
    private final MasterReceiverService masterReceiverService;
    private final ButtonReceiverService buttonReceiverService;
    private final JoinReceiverService joinReceiverService;

    private final VoiceReceiverService voiceReceiverService;

    @Getter
    private static Guild guild;
    @Getter
    private static JDA jda;

    public MafiaBot(JDA discordClient,
                    MessageReceiverService messageReceiverService,
                    MasterReceiverService masterReceiverService, ButtonReceiverService buttonReceiverService,
                    JoinReceiverService joinReceiverService, VoiceReceiverService voiceReceiverService) {
        this.messageReceiverService = messageReceiverService;
        this.discordClient = discordClient;
        this.masterReceiverService = masterReceiverService;
        this.buttonReceiverService = buttonReceiverService;
        this.joinReceiverService = joinReceiverService;
        this.voiceReceiverService = voiceReceiverService;
    }

    @SneakyThrows
    public void init() {
        this.jda = discordClient;
        discordClient.addEventListener(messageReceiverService, voiceReceiverService,buttonReceiverService, joinReceiverService);
        discordClient.awaitReady();
    }
}

