package ru.devsett.bot.service.comand.mafia;

import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.db.service.impl.GameHistoryService;

@CommandInfo(name = "Игра",
        description = "Результат выбранной игры",
        requirements = "Номер игры")
@Component
public class HistoryGameCommand extends MyCommand {

    private final GameHistoryService gameHistoryService;
    private final DiscordService discordService;

    public HistoryGameCommand(GameHistoryService gameHistoryService, DiscordService discordService) {
        this.gameHistoryService = gameHistoryService;
        this.discordService = discordService;

        this.name = "игра";
        this.help = "результат выбранной игры";
        this.guildOnly = false;
    }

    @Override
    public void execute(MessageReceivedEvent event, String command) {
        var cmd = command.replaceAll("\\s+"," ").split(" ");
        Integer num = Integer.valueOf(cmd[1]);

        var game = gameHistoryService.getGameById(num);

        discordService.sendChatEmbedTemp(event, "Игра №"+num,
                "Победа "+ (game.isWinRed()?"Красных":"Черных"),null);
    }

}
