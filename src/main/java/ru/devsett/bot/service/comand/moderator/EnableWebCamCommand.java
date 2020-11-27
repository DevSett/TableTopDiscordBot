package ru.devsett.bot.service.comand.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.ChannelService;
import ru.devsett.db.service.impl.ConfigService;

@Component
public class EnableWebCamCommand extends MyCommand {

    private final ChannelService channelService;
    private final DiscordService discordService;
    private final ConfigService configService;

    public EnableWebCamCommand(ChannelService channelService, DiscordService discordService, ConfigService configService) {
        this.channelService = channelService;
        this.discordService = discordService;
        this.configService = configService;

        this.requiredRole = Role.MODERATOR.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var ent = configService.revertWebCam();
        commandEvent.reply("статус: " + ent.isEnabled());
    }

    @Override
    public String name() {
        return "вебкам";
    }

    @Override
    public String help() {
        return "служебная команда для проверки на мут от ведущего";
    }

}
