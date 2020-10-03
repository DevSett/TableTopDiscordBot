package ru.devsett.bot.service.comand.developer.game.classic;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.bot.util.Role;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.UserService;
import ru.devsett.db.service.impl.WinRateClassicService;
import ru.devsett.db.service.impl.WinRateService;

@Component
public class CancelClassicCommand extends MyCommand {
    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final WinRateClassicService winRateClassicService;
    private final GameHistoryService gameHistoryService;

    public CancelClassicCommand(DiscordService discordService, UserService userService, UtilService utilService, WinRateClassicService winRateClassicService, GameHistoryService gameHistoryService) {
        this.discordService = discordService;
        this.userService = userService;
        this.utilService = utilService;
        this.winRateClassicService = winRateClassicService;
        this.gameHistoryService = gameHistoryService;


        requiredArgs = 1;
        this.requiredRole = Role.DEVELOPER.getName();
    }


    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        Integer num = utilService.getRate(splitArgs[0]);

        var game = gameHistoryService.getGameById(num);

        if (game == null  || !game.isClassic()) {
            commandEvent.reply("Не найдена игра");
            return;
        }

        var whos = gameHistoryService.getAllWho(game);

        for (WhoPlayerHistoryEntity who : whos) {
            if (game.isWinRed()) {
                winRateClassicService.removeDonLose(game.getDonPlayer());
                winRateClassicService.removeSheriffWin(game.getSheriffPlayer());
                if (who.isRedPlayer()) {
                    winRateClassicService.removeRedWin(who.getPlayer());
                } else {
                    winRateClassicService.removeBlackLose(who.getPlayer());
                }
            } else {
                winRateClassicService.removeDonWin(game.getDonPlayer());
                winRateClassicService.removeSheriffLose(game.getSheriffPlayer());
                if (who.isRedPlayer()) {
                    winRateClassicService.removeRedLose(who.getPlayer());
                } else {
                    winRateClassicService.removeBlackWin(who.getPlayer());
                }
            }
        }

        gameHistoryService.deleteGame(game.getId());
        commandEvent.reply("Кл. не засчитано в " + game.getId());
    }

    @Override
    public String name() {
        return "отменак";
    }


    @Override
    public String help() {
        return "отмена игры";
    }
}
