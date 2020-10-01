package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.games.RangService;

@Component
public class TopSityCommand extends MyCommand {

    private final DiscordService discordService;
    private final RangService rangService;

    public TopSityCommand(DiscordService discordService, RangService rangService) {
        this.discordService = discordService;
        this.rangService = rangService;
    }

    @Override
    public String name() {
        return "топ";
    }

    @Override
    public String help() {
        return "текущий топ на городской мафии по правилам ФИИМ";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendChatEmbedTemp(event, "Топ игроков в мафию по городу",
                null, null, rangService.getTopWinRate(), 30);
    }



}
