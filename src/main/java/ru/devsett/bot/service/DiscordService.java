package ru.devsett.bot.service;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.intefaces.NickNameEvent;
import ru.devsett.bot.service.receiver.MessageReceiverService;
import ru.devsett.bot.util.ActionDo;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Field;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DiscordService {

    private final MessageService messageService;
    private final UserService userService;

    public DiscordService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    public ActionDo addOrRemoveRole(MessageCreateEvent event, Role role) {
        return addOrRemoveRole(event.getGuild().block(), event.getMember(), role);
    }

    public ActionDo addOrRemoveRole(Guild guild, Optional<Member> member, Role role) {
        if (!member.isPresent()) {
            return ActionDo.NOTHING;
        }

        var findedRole = guild.getRoles()
                .filter(roleDiscord -> roleDiscord.getName().equals(role.getName()))
                .blockFirst();

        if (findedRole == null) {
            return ActionDo.NOTHING;
        }

        var isPresentRole = member.get().getRoles()
                .any(roleDiscord -> findedRole.getId().equals(roleDiscord.getId()))
                .block();
        if (isPresentRole) {
            member.get().removeRole(findedRole.getId()).block();
            return ActionDo.REMOVE;
        } else {
            member.get().addRole(findedRole.getId()).block();
            return ActionDo.ADD;
        }
    }

    public String changeNickName(MessageCreateEvent event, Member member, NickNameEvent nickNameEvent) {
        var newNickName = nickNameEvent.getName(getNickName(member));
        try {
            member.edit(spec -> spec.setReason("play mafia").setNickname(newNickName)).block();
            return newNickName;
        } catch (ClientException e) {
            if (e.getStatus() == HttpResponseStatus.FORBIDDEN) {
                sendChat(event, "Недостаточно прав для изминения имени на " + newNickName);
            }
        }
        return "";
    }

    public Boolean isPresentRole(MessageCreateEvent event, Role... roles) {
        if (roles.length == 0) {
            return false;
        }
        return event.getMember().get()
                .getRoles()
                .any(roleDiscord -> Arrays.stream(roles).anyMatch(role -> role.getName().equals(roleDiscord.getName())))
                .block();
    }

    public Boolean isPresentPermission(MessageCreateEvent event, Role role, Permission... permissions) {
        if (permissions.length == 0) {
            return true;
        }

        return event.getMember().get()
                .getRoles()
                .filter(roleDiscord -> role.getName().equals(roleDiscord.getName()))
                .blockFirst().getPermissions().stream().anyMatch(perm -> Arrays.asList(permissions).contains(perm));
    }

    public List<Member> getChannelPlayers(MessageCreateEvent event,
                                          String... excludeMembers) {
        return getChannelPlayers(getChannel(event), excludeMembers);
    }

    public List<Member> getChannelPlayers(VoiceChannel channel,
                                          String... excludeMembers) {
        return channel.getVoiceStates()
                .map(st -> st.getMember().block())
                .filter(member -> excludeMembers.length <= 0 || Arrays.stream(excludeMembers)
                        .anyMatch(exc -> !getNickName(member).startsWith(exc))
                ).collectList().block();
    }

    public VoiceChannel getChannel(MessageCreateEvent event) {
        try {
            return event.getMember()
                    .orElseThrow(NullPointerException::new)
                    .getVoiceState()
                    .map(VoiceState::getChannelId)
                    .map(id -> MafiaBot.getGuild().getChannelById(id.orElseThrow(NullPointerException::new)))
                    .block().cast(VoiceChannel.class).block();
        } catch (NullPointerException e) {
            throw new DiscordException("Войс канал не найден или недостаточно прав!");
        }
    }

    public void randomOrderPlayers(MessageCreateEvent messageCreateEvent, List<Member> channelPlayers) {
        var members = channelPlayers.stream().collect(Collectors.toList());
        List<Integer> membersNumbers = new ArrayList<>();
        var random = new SecureRandom();
        for (Member member : members) {
            var number = random.nextInt(members.size()) + 1;
            while (membersNumbers.contains(number)) {
                number = random.nextInt(members.size()) + 1;
            }
            membersNumbers.add(number);
            int finalNumber = number;

            var nickName = getNickName(member);
            var newNickName = "";
            if (nickName.length() > 3 && nickName.toCharArray()[2] == '.' && isOrder(nickName.substring(0, 2))) {
                newNickName = changeNickName(messageCreateEvent, member, name -> name.substring(3));
            }
            String finalNewNickName = newNickName;
            changeNickName(messageCreateEvent, member, name -> numberString(finalNumber) + (finalNewNickName.isEmpty() ? name : finalNewNickName));
        }
    }

    private boolean isOrder(String str) {
        try {
            Integer.valueOf(str);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private String numberString(Integer number) {
        if (number < 10) {
            return "0" + number + ". ";
        } else {
            return number + ". ";
        }
    }

    public String getNickName(Member member) {
        return member.getNickname().orElse(member.getDisplayName());
    }

    public void sendPrivateMessage(MessageCreateEvent event, Member member, String msg) {
        try {
            if (msg.length() > 1999) {
                msg = msg.substring(msg.length() - 1999);
            }
            member.getPrivateChannel().block().createMessage(msg).block();
            messageService.sendMessage(member, msg);
        } catch (ClientException e) {
            if (e.getStatus() == HttpResponseStatus.FORBIDDEN) {
                sendChat(event, "Недостаточно прав для отправки сообщения для пользователя " + member.getUsername());
            }
        }
    }

    public void sendChat(MessageCreateEvent event, String s) {
        event.getMessage().getChannel().block().createMessage(s).block();
    }

    public Member getPlayerByStartsWithNick(List<Member> members, String nick) {
        return members.stream().filter(player -> getNickName(player).startsWith(nick)).findFirst()
                .orElseThrow(() -> new DiscordException("Не найден никнейм " + nick));
    }

    public void unmuteall(MessageCreateEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            player.edit(pl -> pl.setMute(false)).block();
        });
    }

    public void muteall(MessageCreateEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            player.edit(pl -> pl.setMute(true)).block();
        });
    }

    public void muteall(VoiceChannel channel) {
        getChannelPlayers(channel).forEach(player -> {
            player.edit(pl -> pl.setMute(true)).block();
        });
    }

    public void unmuteall(VoiceChannel channel) {
        getChannelPlayers(channel).forEach(player -> {
            player.edit(pl -> pl.setMute(false)).block();
        });
    }

    public void sendChatEmbed(MessageCreateEvent event, String title, String msgHelp, String url) {
        sendChatEmbed(event, title, msgHelp, url, Collections.emptyList());
    }

    public void sendChatEmbed(MessageCreateEvent event, String title, String msgHelp, String url, List<Field> fields) {
        event.getMessage().getChannel().block().createEmbed(emd -> {
            emd.setTitle(title);
            if (msgHelp != null) {
                emd.setDescription(msgHelp);
            }
            if (url != null) {
                emd.setUrl(url);
            }
            if (!fields.isEmpty()) {
                for (Field field : fields) {
                    emd.addField(field.getName(), field.getValue(), field.isInline());
                }
            }
        }).block();
    }

    public void ban(MessageCreateEvent event, String userName, String reason, int hours) {
        var findedMember = event.getGuild().block().getMembers().filter(member -> member.getUsername().equals(userName)).blockFirst();
        if (findedMember == null) {
            throw new DiscordException("Пользователь не найден!");
        } else {
            userService.ban(findedMember, event.getMember().get(), hours);
            var days = Math.min(7, hours / 24);
            findedMember.ban(spec -> spec.setReason(reason).setDeleteMessageDays(days)).block();
            sendChat(event, "Выдан бан!");
        }
    }

    public void hideban(MessageCreateEvent event, String userName, int hours) {
        var findedMember = event.getGuild().block().getMembers().filter(member -> member.getUsername().equals(userName)).blockFirst();
        if (findedMember == null) {
            throw new DiscordException("Пользователь не найден!");
        } else {
            userService.ban(findedMember, event.getMember().get(), hours);
            addOrRemoveRole(event.getGuild().block(), Optional.ofNullable(findedMember), Role.BAN);
            sendChat(event, "Выдан бан!");
        }
    }

    public void fastban(MessageCreateEvent event, String name, String reason, int hours) {
        var member = getPlayerByStartsWithNick(getChannelPlayers(event), name);
        userService.ban(member, event.getMember().get(), hours);
        var days = Math.min(7, hours / 24);
        member.ban(spec -> spec.setReason(reason).setDeleteMessageDays(days <= 1 ? 1 : days)).block();
    }

    public void hidefastban(MessageCreateEvent event, String name, int hours) {
        var member = getPlayerByStartsWithNick(getChannelPlayers(event), name);
        userService.ban(member, event.getMember().get(), hours);
        addOrRemoveRole(event.getGuild().block(), Optional.ofNullable(member), Role.BAN);
    }

    public void unban(MessageCreateEvent event, String userName) {
        var findedMember = event.getGuild().block().getBans().filter(
                ban -> ban.getUser().getUsername().equals(userName)).blockFirst();

        var user = userService.findByUserName(userName);
        Member member = null;
        if (user != null) {
            member = event.getGuild().block().getMemberById(Snowflake.of(user.getId())).block();
        }
        if (findedMember == null && member == null) {
            throw new DiscordException("Пользователь не найден!");
        } else {
            if (findedMember != null) {
                userService.unban(userService.findById(findedMember.getUser().getId().asLong()));
                addOrRemoveRole(event.getGuild().block(),
                        Optional.ofNullable(findedMember.getUser().asMember(event.getGuildId().get()).block()), Role.BAN);
                event.getGuild().block().unban(Snowflake.of(findedMember.getUser().getId().asLong())).block();
            } else {
                userService.unban(userService.findById(member.getId().asLong()));
                addOrRemoveRole(event.getGuild().block(), Optional.ofNullable(member), Role.BAN);
            }
            sendChat(event, "Выпущен из клетки!");

        }
    }

    public void toLogTextChannel(String title, String description, MessageCreateEvent event, Color color) {
        var name = "Неизвестно";
        var currentName = "Неизвестно";

        try {
            var member = event.getMember().orElse(null);
            name = member != null ? member.getUsername() : name;
            var current = event.getMessage().getChannel().block();
            if (current instanceof TextChannel) {
                currentName = ((TextChannel) current).getName();
            }
        } catch (Exception ignored) {
        }
        var footer = "Юзер: " + name + ", Канал: " + currentName;
        toLog(title, footer, description, color);

    }

    public void toLogVoiceChannel(String title, String description, VoiceStateUpdateEvent event, Color color) {
        var name = "Неизвестно";
        var currentName = "Неизвестно";

        try {
            var member = event.getCurrent().getMember().block();
            name = member != null ? member.getUsername() : name;
            var current = event.getCurrent().getChannel().block();
            currentName = current != null ? current.getName() : currentName;
        } catch (Exception ignored) {
        }
        var footer = "Юзер: " + name + ", Канал: " + currentName;

        toLog(title, footer, description, color);
    }

    public void toLog(String title, String footer, String description, Color color) {
        try {
            var channel = MafiaBot.getGuild().getChannels().filter(chan -> chan.getName().equals("log")).blockFirst();

            if (!(channel instanceof TextChannel)) {
                return;
            }

            var textChannel = (TextChannel) channel;

            textChannel.createEmbed(spec -> {
                spec.setTitle(title)
                        .setDescription(description)
                        .setFooter(footer, null);
                if (color != null) {
                    spec.setColor(color);
                }
            }).block();

        } catch (Exception e2) {
            log.error(e2);
        }
    }
}
