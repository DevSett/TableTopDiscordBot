package ru.devsett.bot.service.comand.developer;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.ChannelService;

@Component
public class ClearTypeCommand extends MyCommand {

    private final ChannelService channelService;

    public ClearTypeCommand(ChannelService channelService) {
        this.channelService = channelService;

        this.requiredRole = Role.DEVELOPER.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        channelService.clearTypes();
    }

    @Override
    public String name() {
        return "clearch";
    }

    @Override
    public String help() {
        return "служебная команда для очистки типов у каналов";
    }
}
