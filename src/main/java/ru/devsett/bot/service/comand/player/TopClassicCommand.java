package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.games.RangService;

@Component
public class TopClassicCommand extends MyCommand {

    private final DiscordService discordService;
    private final RangService rangService;

    public TopClassicCommand(DiscordService discordService, RangService rangService) {
        this.discordService = discordService;
        this.rangService = rangService;
        this.aliases = new String[]{"архитектор"};
    }

    @Override
    public String name() {
        return "топк";
    }

    @Override
    public String help() {
        return "текущий топ на классической мафии по правилам ФИИМ";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendChatEmbedTemp(event, "Топ игроков в мафию по классике",
                null, null, rangService.getTopWinRateK(), 30);
    }

}
