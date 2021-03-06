package ru.devsett.bot.service.comand.developer.game.classic;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.UserService;
import ru.devsett.db.service.impl.WinRateClassicService;
import ru.devsett.db.service.impl.WinRateService;

@Component
public class LoseRedClassicCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final WinRateClassicService winRateClassicService;

    public LoseRedClassicCommand(DiscordService discordService, UserService userService, UtilService utilService,
                                     WinRateClassicService winRateClassicService) {
        this.discordService = discordService;
        this.userService = userService;
        this.utilService = utilService;
        this.winRateClassicService = winRateClassicService;

        requiredArgs = 1;
        this.requiredRole = Role.DEVELOPER.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        for (int i = 0; i < splitArgs.length; i++) {
            var player = splitArgs[i];
            var id = utilService.getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = event.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateClassicService.addRedLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за красных " + user.getUserName(), rate.getMafiaLoseRed() + "", null);
            }

        }
    }

    @Override
    public String name() {
        return "п-красныек";
    }


    @Override
    public String help() {
        return "засчитать порожение красным выбранным игрокам, на классической мафии. №-n аргумент - теги игроков";
    }
}
