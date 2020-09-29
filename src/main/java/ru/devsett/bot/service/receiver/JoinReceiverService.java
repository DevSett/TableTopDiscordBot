package ru.devsett.bot.service.receiver;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.db.service.impl.InviteService;
import ru.devsett.db.service.impl.UserService;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
public class JoinReceiverService extends ListenerAdapter {

    private final InviteService inviteService;
    private final UserService userService;
    private final DiscordService discordService;

    public JoinReceiverService(InviteService inviteService, UserService userService, DiscordService discordService) {
        this.inviteService = inviteService;
        this.userService = userService;
        this.discordService = discordService;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        var user = userService.findById(event.getMember().getIdLong());
        if (user != null) {
            return;
        } else {
            user = userService.getOrNewUser(event.getMember());
        }

        List<Invite> invites = null;
        try {
            invites = event.getGuild().retrieveInvites().submit().get();
        } catch (Exception e) {
            discordService.toLog(event.getGuild(), "Приглашения", null, e.getMessage(), Color.red.getRGB());
        }
        for (Invite invite : invites) {
            try {
                var member = event.getGuild().retrieveMember(invite.getInviter()).submit().get();
                var inviter = userService.getOrNewUser(member);
                var inviteEntity = inviteService.getInvite(invite.getCode());
                if (inviteEntity == null) {
                    inviteService.addInvite(invite.getCode(), invite.getUses(), inviter);
                } else if (inviteEntity.getCount() < invite.getUses()) {
                    inviteService.updateCount(invite.getUses(), inviteEntity);
                    userService.addRating(event.getGuild(), inviter, 20, "Inviter за " + user.getUserName(),
                            discordService);
                    discordService.sendToJoinRoom(member.getAsMention()
                                    + " вам начислено 20 коинов за приглашение " + event.getMember().getEffectiveName(),
                            event.getGuild());
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onGuildInviteCreate(@Nonnull GuildInviteCreateEvent event) {
        try {
            var member = event.getGuild().retrieveMember(event.getInvite().getInviter()).submit().get();
            var user = userService.getOrNewUser(member);
            inviteService.addInvite(event.getCode(), user);
        } catch (Exception e) {
            discordService.toLog(event.getGuild(), "Создание приглашение", event.getUrl(), "Ошибка", Color.RED.getRGB());
        }
    }

    @Override
    public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event) {
        inviteService.deleteInvite(event.getCode());
    }
}
