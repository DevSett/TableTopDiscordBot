package ru.devsett.bot.service.receiver;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.rest.util.Color;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.RangService;


@Service
@Log4j2
public class VoiceReceiverService {

    private final RangService rangService;
    private final DiscordService discordService;

    public VoiceReceiverService(RangService rangService, DiscordService discordService) {
        this.rangService = rangService;
        this.discordService = discordService;
    }

    public void consume(VoiceStateUpdateEvent event) {
        try {
            if (event.getCurrent() != null && event.getOld().isEmpty()) {
                join(event);
            }
            if (event.getCurrent() != null && event.getOld().isPresent()) {
                swap(event);
            }
        } catch (Exception e) {
            discordService.toLogVoiceChannel("Voice Exception", e.getMessage(), null, Color.RED);
        }
    }

    private void join(VoiceStateUpdateEvent event) {
        rangService.join(event.getCurrent());
    }

    private void swap(VoiceStateUpdateEvent event) {
        rangService.exit(event.getCurrent(), event.getOld().get());
    }
}
