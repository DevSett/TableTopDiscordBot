package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.UserService;

import java.security.SecureRandom;

@Component
public class CasinoHalfCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public CasinoHalfCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;

        requiredArgs = 1;
        cooldown = 5;
    }

    @Override
    public String name() {
        return "казино-фифти";
    }

    @Override
    public String help() {
        return "делайте ставку. Шанс выйгрыша 50/50, поставленные койны удвоятся. 1-й аргумент кол-во коинов";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var random = new SecureRandom();

        var value = utilService.getRate(splitArgs[0]);
        var user = userService.getOrNewUser(event.getMember());

        if (value > user.getRating() || value < 1) {
            commandEvent.reply("Недостаточно средств!");
            return;
        }

        if (random.nextInt(2) == 1) {
            commandEvent.reply("Поздравляем вы выиграли " + value);
            userService.addRating(event.getGuild(), user, value, name, discordService);
        } else {
            commandEvent.reply("Упс, вы проиграли " + value);
            userService.addRating(event.getGuild(), user, -1 * value, name, discordService);
        }
    }
}