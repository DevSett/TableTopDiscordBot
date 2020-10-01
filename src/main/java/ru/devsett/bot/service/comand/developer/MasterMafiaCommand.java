package ru.devsett.bot.service.comand.developer;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.util.Emoji;
import ru.devsett.bot.util.Role;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.db.service.impl.ChannelService;

@Component
public class MasterMafiaCommand extends MyCommand {

    private final ChannelService channelService;
    private final DiscordService discordService;

    public MasterMafiaCommand(ChannelService channelService, DiscordService discordService) {
        this.channelService = channelService;
        this.discordService = discordService;

        this.requiredRole = Role.DEVELOPER.getName();
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        discordService.sendChat(event.getTextChannel(), "Создать классическую мафию", Emoji.GAME);
        discordService.sendChat(event.getTextChannel(), "Создать городскую мафию", Emoji.GAME);

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.MASTER_CHANNEL);
    }

    @Override
    public String name() {
        return "создать-мастер";
    }

    @Override
    public String help() {
        return "служебная команда для создания канала, который создает игры в мафию";
    }
}
