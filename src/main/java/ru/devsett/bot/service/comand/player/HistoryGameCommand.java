package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.service.impl.GameHistoryService;

@Component
public class HistoryGameCommand extends MyCommand {

    private final GameHistoryService gameHistoryService;
    private final DiscordService discordService;
    private final UtilService utilService;

    public HistoryGameCommand(GameHistoryService gameHistoryService, DiscordService discordService, UtilService utilService) {
        this.gameHistoryService = gameHistoryService;
        this.discordService = discordService;
        this.utilService = utilService;

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
        Integer num = utilService.getRate(splitArgs[0]);

        var game = gameHistoryService.getGameById(num);

        if (game == null || !game.isEndGame()) {
            commandEvent.reply("Не найдена игра");
            return;
        }

        var don = game.getDonPlayer().getId();
        var sheriff = game.getSheriffPlayer().getId();
        var whos = gameHistoryService.getAllWho(game);

        StringBuilder builder = new StringBuilder();
        for (WhoPlayerHistoryEntity who : whos) {
            if (who.isRedPlayer()){
                builder.append("\n<@!").append(who.getPlayer().getId()).append("> - Красный");
            } else {
                builder.append("\n<@!").append(who.getPlayer().getId()).append("> - Черный");
            }
        }

        var type = game.isClassic()?"классическая":"городская";
        discordService.sendChatEmbed(event, "Игра "+type+" №"+num,
                "Победа "+ (game.isWinRed()?"Красных":"Черных")
                        + "\n<@!"+don+"> - Дон"
                        + "\n<@!"+sheriff+"> - Шериф"
                        + builder.toString()
                ,null);
    }



}
