package ru.devsett.bot.service;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.games.RangService;
import ru.devsett.db.service.WatchmanService;

@Service
public class VoiceReceiverService {

    @Autowired
    private RangService rangService;


    public void consume(VoiceStateUpdateEvent event) {
        if (event.getCurrent() != null && event.getOld().isEmpty()) {
            join(event);
        }
        if (event.getCurrent() != null && event.getOld().isPresent()) {
            swap(event);
        }
    }

    private void join(VoiceStateUpdateEvent event) {
        rangService.join(event.getCurrent());
    }

    private void swap(VoiceStateUpdateEvent event) {
        rangService.exit(event.getCurrent(), event.getOld().get());
    }
}
