package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.UserService;

@Component
public class GiveCoinCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public GiveCoinCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;

        this.requiredArgs = 2;
    }

    @Override
    public String name() {
        return "отдать-коины";
    }

    @Override
    public String help() {
        return "отдать коины выбранному игроку. 1-й аргумент тег. 2-й кол-во коинов.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {

        var user = userService.findById(utilService.getId(splitArgs[0]));
        if (user == null) {
            discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
        } else {
            var number = utilService.getRate(splitArgs[1]);
            var godUser = userService.getOrNewUser(event.getMember());
            if (godUser.getRating() < number) {
                discordService.sendChat(event.getTextChannel(), "Недостаточно средств!");
                return;
            }
            var from = "<@!" + event.getMember().getIdLong() + ">";
            var sendedRate = userService.addRating(event.getGuild(), user, number,
                    from, discordService);
            godUser = userService.getOrNewUser(event.getMember());
            var fallRate = userService.addRating(event.getGuild(), godUser, -1 * number, from, discordService);

            discordService.sendChatEmbedTemp(event, ":moneybag: Баланс " + sendedRate.getUserName(), sendedRate.getRating() + "", null);
            discordService.sendChatEmbedTemp(event, ":moneybag: Баланс " + fallRate.getUserName(), fallRate.getRating() + "", null);
        }
    }



}
