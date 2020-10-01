package ru.devsett.bot.service.comand.developer;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.ChannelService;
import ru.devsett.db.service.impl.GameHistoryService;

@Component
public class ClearDontStopGameCommand extends MyCommand {

    private final GameHistoryService gameHistoryService;

    public ClearDontStopGameCommand(GameHistoryService gameHistoryService) {
        this.gameHistoryService = gameHistoryService;

        this.requiredRole = Role.DEVELOPER.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        gameHistoryService.deleteAllStopGames();
    }

    @Override
    public String name() {
        return "clearh";
    }

    @Override
    public String help() {
        return "служебная команда для очистки не остановленных игр";
    }
}
