package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.db.service.impl.GameHistoryService;

@Component
public class HistoryGameCommand extends MyCommand {

    private final GameHistoryService gameHistoryService;
    private final DiscordService discordService;

    public HistoryGameCommand(GameHistoryService gameHistoryService, DiscordService discordService) {
        this.gameHistoryService = gameHistoryService;
        this.discordService = discordService;

        this.requiredArgs = 1;
    }

    @Override
    public String name() {
        return "игра";
    }

    @Override
    public String help() {
        return "результат выбранной игры мафии";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        Integer num = Integer.valueOf(splitArgs[0]);

        var game = gameHistoryService.getGameById(num);

        if (game == null) {
            commandEvent.reply("Не найдена игра");
            return;
        }

        discordService.sendChatEmbedTemp(event, "Игра №"+num,
                "Победа "+ (game.isWinRed()?"Красных":"Черных"),null);
    }



}
