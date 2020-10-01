package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.games.RangService;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.UserService;

@Component
public class WinRateClassicCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final RangService rangService;
    private final UtilService utilService;

    public WinRateClassicCommand(DiscordService discordService, UserService userService, RangService rangService, UtilService utilService) {
        this.userService = userService;
        this.discordService = discordService;
        this.rangService = rangService;
        this.utilService = utilService;
    }

    @Override
    public String name() {
        return "винрейтк";
    }

    @Override
    public String help() {
        return "текущий винрейт на классической мафии по правилам ФИИМ";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            discordService.sendChatEmbedTemp(event, "Ваш винрейт классика",
                    null, null, rangService.getWinRateK(userService.getOrNewUser(event.getMember())),30);
        } else {
            var user = userService.findById(utilService.getId(splitArgs[0]));
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                discordService.sendChatEmbedTemp(event, "Винрейт классики " + user.getUserName(), null, null, rangService.getWinRateK(user),30);
            }
        }
    }



}
