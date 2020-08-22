package ru.devsett.bot.service.receiver;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.RangService;

import java.awt.*;


@Service
@Log4j2
public class VoiceReceiverService extends ListenerAdapter {

    private final RangService rangService;
    private final DiscordService discordService;

    public VoiceReceiverService(RangService rangService, DiscordService discordService) {
        this.rangService = rangService;
        this.discordService = discordService;
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        try {
            join(event);
        } catch (Exception e) {
            discordService.toLogVoiceChannel("Voice Exception", e.getMessage(), event.getMember(),
                    event.getChannelJoined(), Color.RED.getRGB());
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        try {
           leave(event);
        } catch (Exception e) {
            discordService.toLogVoiceChannel("Voice Exception", e.getMessage(), event.getMember(),
                    event.getChannelLeft(),  Color.RED.getRGB());
        }
    }

    private void join(GuildVoiceJoinEvent event) {
        rangService.join(event.getMember(), event.getChannelJoined(), event.getChannelLeft());
    }

    private void leave(GuildVoiceLeaveEvent event) {
        rangService.exit(event.getMember(), event.getChannelJoined(), event.getChannelLeft());
    }
}
