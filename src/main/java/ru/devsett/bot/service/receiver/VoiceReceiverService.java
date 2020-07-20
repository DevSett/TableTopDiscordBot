package ru.devsett.bot.service.receiver;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.rest.http.client.ClientException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.games.RangService;

@Service
@Log4j2
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
        try {
        rangService.join(event.getCurrent());
        } catch (ClientException e) {
            if (e.getStatus() == HttpResponseStatus.FORBIDDEN) {

            }
        }
    }

    private void swap(VoiceStateUpdateEvent event) {
        rangService.exit(event.getCurrent(), event.getOld().get());
    }
}
