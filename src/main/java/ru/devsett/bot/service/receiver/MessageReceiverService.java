package ru.devsett.bot.service.receiver;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.config.DiscordConfig;
import ru.devsett.db.service.impl.ChannelService;
import ru.devsett.db.service.impl.UserService;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
@Log4j2
public class MessageReceiverService extends ListenerAdapter {

    private final DiscordConfig discordConfig;
    private final DiscordService discordService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageReceiverService(DiscordService discordService,
                                  DiscordConfig discordConfig,
                                  UserService userService, ChannelService channelService) {
        this.discordService = discordService;
        this.discordConfig = discordConfig;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        consume(event);
    }

    public void consume(MessageReceivedEvent event) {
        try {
            if (event.isFromType(ChannelType.PRIVATE)) {
                return;
            }
            Message message = event.getMessage();
            String content = message.getContentRaw();

            bump(event, content);
        } catch (Exception e) {
            discordService.toLogTextChannel("Message Exception", e.getMessage(), event, Color.RED.getRGB());
        }
    }

    private void bump(MessageReceivedEvent event, String content) {
        if (event.getAuthor() == null) {
            return;
        }
        if (isServerMonitoring(event)) {
            if (event.getMessage().getEmbeds().size() == 0) {
                return;
            }
            var emb = event.getMessage().getEmbeds().get(0);
            if (emb.getDescription().isEmpty()) {
                return;
            }
            var desc = emb.getDescription();
            if (desc.contains("Server bumped by") && desc.contains("<") && desc.contains(">")) {
                var user = userService.findById(Long.parseLong(desc.substring(desc.indexOf("<") + 2, desc.indexOf(">"))));
                if (user != null) {
                    userService.addRating(event.getGuild(), user, 100, "!bump", discordService);
                }
            }
        } else {
            if (!event.getAuthor().isBot() && content.equals("!bump")) {
                userService.getOrNewUser(event.getMember());
          }
        }
    }

    private boolean isServerMonitoring(MessageReceivedEvent event) {
        return event.getAuthor().isBot()
                && event.getAuthor().getName().equals("Server Monitoring");
    }

}
