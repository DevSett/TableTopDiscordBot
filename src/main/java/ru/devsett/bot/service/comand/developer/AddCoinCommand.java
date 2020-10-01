package ru.devsett.bot.service.comand.developer;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.UserService;

@Component
public class AddCoinCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public AddCoinCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.discordService = discordService;
        this.userService = userService;
        this.utilService = utilService;

        requiredArgs = 1;
        this.requiredRole = Role.DEVELOPER.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        if (event.getMember() == null)  {
           throw new DiscordException("Не найден участник!");
        }

        if (splitArgs.length == 1) {
            var number = utilService.getRate(splitArgs[0]);
            var user = userService.getOrNewUser(event.getMember());
            var newRate = userService.addRating(event.getGuild(), user, number,
                    "<@!" + event.getMember().getIdLong() + ">", discordService).getRating();
            discordService.sendChatEmbedTemp(event, "Ваш баланс", ":moneybag: " + newRate + "", null);
        } else if (splitArgs.length > 1) {
            var user = userService.findById(utilService.getId(splitArgs[0]));
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                var number = utilService.getRate(splitArgs[1]);
                var rate = userService.addRating(event.getGuild(), user, number,
                        "<@!" + event.getMember().getIdLong() + ">", discordService).getRating();
                discordService.sendChatEmbedTemp(event, "Баланс " + user.getUserName(), ":moneybag: " + rate + "", null);
            }
        }
    }

    @Override
    public String name() {
        return "адд-коины";
    }


    @Override
    public String help() {
        return "команда для выдачи коинов. 1-й аргумент тег. 2-й аргумент кол-во.";
    }
}
