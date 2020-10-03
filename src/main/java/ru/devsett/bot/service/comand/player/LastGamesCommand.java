package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.UserService;

import java.util.stream.Collectors;

@Component
public class LastGamesCommand extends MyCommand {

    private final GameHistoryService gameHistoryService;
    private final DiscordService discordService;
    private final UtilService utilService;
    private final UserService userService;

    public LastGamesCommand(GameHistoryService gameHistoryService, DiscordService discordService, UtilService utilService, UserService userService) {
        this.gameHistoryService = gameHistoryService;
        this.discordService = discordService;
        this.utilService = utilService;
        this.userService = userService;
    }

    @Override
    public String name() {
        return "ласт";
    }

    @Override
    public String help() {
        return "результат выбранной игры мафии";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var donGames = gameHistoryService.getLastDonGames(userService.getOrNewUser(event.getMember()));
        var sheriffGames = gameHistoryService.getLastSheriffGames(userService.getOrNewUser(event.getMember()));
        var redGames = gameHistoryService.getLastRedGames(userService.getOrNewUser(event.getMember()));
        var blackGames = gameHistoryService.getLastBlackGames(userService.getOrNewUser(event.getMember()));

        discordService.sendChatEmbed(event, "Номера последних ваших игр",
                "Игры за красных: " + redGames.stream().map(String::valueOf).collect(Collectors.joining(", "))
                +"\nИгры за черных: " + blackGames.stream().map(String::valueOf).collect(Collectors.joining(", "))
                +"\nИгры за кома: " + sheriffGames.stream().map(String::valueOf).collect(Collectors.joining(", "))
                +"\nИгры за дона: " + donGames.stream().map(String::valueOf).collect(Collectors.joining(", "))
                , null);
    }



}
