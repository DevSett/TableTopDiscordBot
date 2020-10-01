package ru.devsett.bot.service.comand.moderator;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.MessageService;

@Component
public class DirtyCommand extends MyCommand {

    private final MessageService messageService;
    private final DiscordService discordService;

    public DirtyCommand(MessageService messageService, DiscordService discordService) {
        this.messageService = messageService;
        this.discordService = discordService;

        this.requiredArgs = 1;
        this.requiredRole = Role.MODERATOR.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendPrivateMessage(event.getMember(), messageService.getAllMessages(splitArgs[0]));
    }

    @Override
    public String name() {
        return "грязь";
    }

    @Override
    public String help() {
        return "служебная команда для получения последних сообщение отправленны пользователю по его никнейму без тега";
    }
}
