package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.UserService;

@Component
public class KissCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public KissCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;

        requiredArgs = 1;
    }

    @Override
    public String name() {
        return "поцелуй";
    }

    @Override
    public String help() {
        return "передайте поцелуй игроку, выбранному игроку передастся от вас 50 коинов. 1-й аргумент тег игрока.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var user = userService.findById(utilService.getId(splitArgs[0]));
        if (user == null) {
            discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
        } else {
            var number = 50;
            var godUser = userService.getOrNewUser(event.getMember());
            if (godUser.getRating() < number) {
                discordService.sendChat(event.getTextChannel(), "Недостаточно средств, целуй свою попу!");
                return;
            }
            var from = "<@!" + event.getMember().getIdLong() + ">";
            var sendedRate = userService.addRating(event.getGuild(), user, number,
                    from, discordService);
            godUser = userService.getOrNewUser(event.getMember());
            var to = "<@!" + sendedRate.getId() + ">";
            userService.addRating(event.getGuild(),godUser, number*-1, from, discordService);

            discordService.sendChatEmbed(event, "В подарке: " + number + " мафкоинов",
                    ":kissing_cat: " +from +" посылает поцелуй для " + to   , null);
        }
    }
}
