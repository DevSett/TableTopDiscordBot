package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.UserService;

@Component
public class BalanceCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;

    public BalanceCommand(DiscordService discordService, UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.aliases = new String[]{"баланс"};
    }

    @Override
    public String name() {
        return "коины";
    }

    @Override
    public String help() {
        return "ваш текущий баланс";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        if (commandEvent.getArgs().isEmpty()) {
            discordService.sendChatEmbedTemp(event, "Ваш баланс",
                    userService.getOrNewUser(event.getMember()).getRating() + "", null);
        } else {
            var user = userService.findById(utilService.getId(splitArgs[0]));
            if (user == null) {
                discordService.sendChatTemp(event.getTextChannel(), "Пользователь не найден!",30);
            } else {
                discordService.sendChatEmbedTemp(event, "баланс " + user.getUserName(), user.getRating() + "", null);
            }
        }
    }



}
