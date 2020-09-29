package ru.devsett.bot.service.receiver;

import lombok.extern.log4j.Log4j2;

import net.dv8tion.jda.api.events.message.react.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.MafiaService;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.db.service.impl.ChannelService;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.WinRateClassicService;
import ru.devsett.db.service.impl.WinRateService;

import javax.annotation.Nonnull;
import java.awt.*;

@Service
@Log4j2
public class ButtonReceiverService extends ListenerAdapter {

    private final DiscordService discordService;
    private final GameHistoryService gameHistoryService;
    private final WinRateService winRateService;
    private final WinRateClassicService winRateClassicService;
    private final MasterReceiverService masterReceiverService;
    private final MafiaService mafiaService;
    private final ChannelService channelService;

    public ButtonReceiverService(DiscordService discordService, GameHistoryService gameHistoryService,
                                 WinRateService winRateService, WinRateClassicService winRateClassicService, MasterReceiverService masterReceiverService, MafiaService mafiaService, ChannelService channelService) {
        this.discordService = discordService;
        this.gameHistoryService = gameHistoryService;
        this.winRateService = winRateService;
        this.winRateClassicService = winRateClassicService;
        this.masterReceiverService = masterReceiverService;
        this.mafiaService = mafiaService;
        this.channelService = channelService;
    }

//    @Override
//    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
//        super.onGuildMemberRoleAdd(event);
//        if (event.getRoles().stream().map(role -> role.getName()).anyMatch(role -> role.equals(Role.MAFCLUB.getName()))) {
//            var ch = channelService.findByType(TypeChannel.JOIN_CHANNEL);
//            if (ch != null) {
//                var textC = event.getGuild().getTextChannelById(ch.getId());
//                discordService.sendChat(textC, "Приветствуем нового друга <@" + event.getMember().getIdLong() + "> в MафКлубе, спасибо что ты с нами!");
//            }
//        }
//    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        try {
            event.retrieveMessage().queue(msg -> {
                if (event.getMember().getUser().isBot()) { //TODO
                    return;
                }

                var textCh = event.getTextChannel();
                var ch = channelService.getOrNewChannel(textCh.getName(), textCh.getIdLong(), false);

                if (ch.getTypeChannel() == TypeChannel.MASTER_CHANNEL) {
                    mafiaService.createGame(msg, event);
                }
            });
        } catch (Exception e) {
            log.error(e);
            discordService.toLog(event.getGuild(), "EmojiException", null, e.getMessage(), Color.RED.getRGB());
        }
    }
}
