package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.UserService;

@Component
public class TopCoinCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;

    public TopCoinCommand(DiscordService discordService, UserService userService) {
        this.userService = userService;
        this.discordService = discordService;

    }

    @Override
    public String name() {
        return "богачи";
    }

    @Override
    public String help() {
        return "топ самых богатых игроков";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendChatEmbedTemp(event, "10 самых богатых игроков",
                null, null, userService.getTopMoney(), 30);
    }



}
