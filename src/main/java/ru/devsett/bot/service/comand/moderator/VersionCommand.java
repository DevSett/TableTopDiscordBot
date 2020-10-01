package ru.devsett.bot.service.comand.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Role;
import ru.devsett.config.DiscordConfig;

@Component
public class VersionCommand extends MyCommand {

    private final DiscordConfig discordConfig;
    private final DiscordService discordService;

    public VersionCommand(DiscordConfig discordConfig, DiscordService discordService) {
        this.discordConfig = discordConfig;
        this.discordService = discordService;

        this.requiredArgs = 1;
        this.requiredRole = Role.MODERATOR.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendChatEmbed(event, "Версия: " + discordConfig.getBuildVersionHide(),
                " Время сборки: " + discordConfig.getBuildTimestamp(), null);
    }

    @Override
    public String name() {
        return "вер";
    }

    @Override
    public String help() {
        return "версия бота";
    }
}
