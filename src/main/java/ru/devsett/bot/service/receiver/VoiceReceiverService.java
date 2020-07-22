package ru.devsett.bot.service.receiver;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.http.client.ClientException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.service.games.RangService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Log4j2
public class VoiceReceiverService {

    private final RangService rangService;

    public VoiceReceiverService(RangService rangService) {
        this.rangService = rangService;
    }

    public void consume(VoiceStateUpdateEvent event) {
        try {
            if (event.getCurrent() != null && event.getOld().isEmpty()) {
                join(event);
            }
            if (event.getCurrent() != null && event.getOld().isPresent()) {
                swap(event);
            }
        } catch (ClientException e) {
            printException(event, e);
        } catch (Exception e) {
            printException(event, e);
        }
    }

    private void printException(VoiceStateUpdateEvent event, Exception e) {
        try {
        var channel = MafiaBot.getGuild().getChannels().filter(chan -> chan.getName().equals("log"))
                .blockFirst();
        if (channel instanceof TextChannel) {
            var member = event.getCurrent().getMember().block();
            var name = member != null ? member.getUsername() : "Неизвестно";
            var current = event.getCurrent().getChannel().block();
            var currentName = current != null ? current.getName() : "Неизвестно";
            ((TextChannel) channel).createEmbed(spec -> spec.setTitle("Voice Exception :" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":")
                    .setDescription(e.getMessage())
                    .setFooter("Юзер: " + name + ", Канал: " + currentName, null))
                    .block();
        }
            log.error(e);
        }catch (Exception e2) {
            log.error(e2);
        }
    }

    private void join(VoiceStateUpdateEvent event) {
        rangService.join(event.getCurrent());
    }

    private void swap(VoiceStateUpdateEvent event) {
        rangService.exit(event.getCurrent(), event.getOld().get());
    }
}
