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
public class WinRateCityCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final RangService rangService;
    private final UtilService utilService;

    public WinRateCityCommand(DiscordService discordService, UserService userService, RangService rangService,
                              UtilService utilService) {
        this.userService = userService;
        this.discordService = discordService;
        this.rangService = rangService;
        this.utilService = utilService;
    }

    @Override
    public String name() {
        return "винрейт";
    }

    @Override
    public String help() {
        return "текущий винрейт на городской мафии";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            discordService.sendChatEmbedTemp(event, "Ваш винрейт город",
                    null, null, rangService.getWinRate(userService.getOrNewUser(event.getMember())),30);
        } else {
            var user = userService.findById(utilService.getId(splitArgs[0]));
            if (user == null) {
                discordService.sendChatTemp(event.getTextChannel(), "Пользователь не найден!", 30);
            } else {
                discordService.sendChatEmbedTemp(event, "Винрейт города " + user.getUserName(), null, null, rangService.getWinRate(user),30);
            }
        }
    }



}
