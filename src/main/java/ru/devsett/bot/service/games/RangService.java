package ru.devsett.bot.service.games;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.TextChannel;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.db.service.ChannelService;
import ru.devsett.db.service.UserService;
import ru.devsett.db.service.WatchmanService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RangService {
    private final UserService userService;
    private final WatchmanService watchmanService;
    private final ChannelService channelService;

    public RangService(UserService userService, WatchmanService watchmanService, ChannelService channelService) {
        this.userService = userService;
        this.watchmanService = watchmanService;
        this.channelService = channelService;
    }

    public void join(VoiceState current) {
        var user = userService.getOrNewUser(current.getMember().block());
        var channel = current.getChannel().block();
        var channelEntity = channelService.getOrNewChannel(channel.getName(), channel.getId().asLong(), true);
        watchmanService.join(channelEntity, user, System.currentTimeMillis());
    }

    public void exit(VoiceState current, VoiceState old) {
        var member = current.getMember().block();
        if (member == null) {
            member = old.getMember().block();
        }
        var user = userService.getOrNewUser(member);
        var channel = old.getChannel().block();
        var channelEntity = channelService.getOrNewChannel(channel.getName(), channel.getId().asLong(), true);
        var watchman = watchmanService.exit(channelEntity, user, System.currentTimeMillis());

        if (watchman != null) {
            var timeSec = (watchman.getExitTime().getTime() - watchman.getJoinTime().getTime()) / 1000;
            var raite = (timeSec * 0.004);
            raite = raite > 116 ? 116 : (int) raite;
            if (raite >= 1) {
                user = userService.addRating(user, (int) raite);

                var textChannel = MafiaBot.getGuild().getChannels().filter(chan -> chan.getName().equals("log"))
                        .blockFirst();

                if (textChannel instanceof TextChannel) {
                    ru.devsett.db.dto.UserEntity finalUser = user;
                    int finalRaite = (int) raite;
                    ((TextChannel) textChannel).createEmbed(spec -> spec.setTitle("Рейтинг")
                            .setDescription("Для игрока " + finalUser.getUserName() + " начислено " + finalRaite + " рейтинга!"  )
                            .setFooter("Рейтинг: " + finalUser.getRating()  +finalRaite, null))
                            .block();
                }
            }
        }

        var channelNew = current.getChannel().block();
        if (channelNew != null) {
            join(current);
        }
    }
}
