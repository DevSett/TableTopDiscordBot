package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.service.impl.UserService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component
public class JackpotCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public List<UserEntity> userEntityList = new ArrayList<>();

    public JackpotCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
    }

    @Override
    public String name() {
        return "джекпот";
    }

    @Override
    public String help() {
        return "делайте ставку вместе с вашими друзьями. Все кто участвуют ставят 100 коинов, можно войти в игру несколько раз. Участников необходимо больше 1, кол-во стаков бесконечное. Для старта розыгрыша пропишите джекпот старт" +
                " 1-й аргумент разыграть среди участников";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var random = new SecureRandom();

        var user = userService.getOrNewUser(event.getMember());

        if (splitArgs[0].equals("старт") && userEntityList.size() > 1) {
            var winUser = userEntityList.get(random.nextInt(userEntityList.size()));
            var win = (100 * userEntityList.size());
            commandEvent.reply("<@!" + winUser.getId() + ">" + " вы выйграли джекпот в " + win + "! Ухух!");
            winUser = userService.findById(winUser.getId());
            userService.addRating(event.getGuild(), winUser, win, "JackPot", discordService);
            userEntityList.clear();
            return;
        }

        if (userEntityList.size() < 2 && splitArgs[0].equals("старт")) {
            commandEvent.reply("Недостаточно участников!");
            return;
        }

        if (100 > user.getRating()) {
            commandEvent.reply("Недостаточно средств!");
            return;
        }

        if (splitArgs[0].isEmpty()) {
            user = userService.addRating(event.getGuild(), user, -100, "JackPot", discordService);
            userEntityList.add(user);
            commandEvent.reply("Вы кинули в копилку джекпота 100 и купили билет!");
            return;
        }


    }
}
