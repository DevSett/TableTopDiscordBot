package ru.devsett.bot.service.comand.developer.game.city;

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
import ru.devsett.db.service.impl.WinRateService;

@Component
public class CancelCityCommand extends MyCommand {
    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final WinRateService winRateService;
    private final GameHistoryService gameHistoryService;

    public CancelCityCommand(DiscordService discordService, UserService userService, UtilService utilService, WinRateService winRateService, GameHistoryService gameHistoryService) {
        this.discordService = discordService;
        this.userService = userService;
        this.utilService = utilService;
        this.winRateService = winRateService;
        this.gameHistoryService = gameHistoryService;


        requiredArgs = 1;
        this.requiredRole = Role.DEVELOPER.getName();
    }


    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        Integer num = utilService.getRate(splitArgs[0]);

        var game = gameHistoryService.getGameById(num);

        if (game == null || game.isClassic()) {
            commandEvent.reply("Не найдена игра");
            return;
        }

        var whos = gameHistoryService.getAllWho(game);

        if (game.isWinRed()) {
            winRateService.removeDonLose(game.getDonPlayer());
            winRateService.removeSheriffWin(game.getSheriffPlayer());
        } else {
            winRateService.removeDonWin(game.getDonPlayer());
            winRateService.removeSheriffLose(game.getSheriffPlayer());
        }

        for (WhoPlayerHistoryEntity who : whos) {
            if (game.isWinRed()) {
                if (who.isRedPlayer()) {
                    winRateService.removeRedWin(who.getPlayer());
                } else {
                    winRateService.removeBlackLose(who.getPlayer());
                }
            } else {
                if (who.isRedPlayer()) {
                    winRateService.removeRedLose(who.getPlayer());
                } else {
                    winRateService.removeBlackWin(who.getPlayer());
                }
            }
        }

        gameHistoryService.deleteGame(game.getId());
        commandEvent.reply("Гор. не засчитано в " + game.getId());
    }

    @Override
    public String name() {
        return "отмена";
    }


    @Override
    public String help() {
        return "отмена игры";
    }
}
