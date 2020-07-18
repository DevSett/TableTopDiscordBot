package ru.devsett.bot.service;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.http.client.ClientException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.NickNameEvent;
import ru.devsett.bot.util.ActionDo;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscordService {

    private final MessageService messageService;

    public DiscordService(MessageService messageService) {
        this.messageService = messageService;
    }

    public ActionDo addOrRemoveRole(MessageCreateEvent event, Role role) {
        var guild = event.getGuild();
        var member = event.getMember().get();

        var findedRole = guild.block().getRoles()
                .filter(roleDiscord -> roleDiscord.getName().equals(role.getName()))
                .blockFirst();

        if (findedRole == null) {
            return ActionDo.NOTHING;
        }

        var isPresentRole = member.getRoles()
                .any(roleDiscord -> findedRole.getId().equals(roleDiscord.getId()))
                .block();
        if (isPresentRole) {
            member.removeRole(findedRole.getId()).block();
            return ActionDo.REMOVE;
        } else {
            member.addRole(findedRole.getId()).block();
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
                sendPrivateMessage(event, member, "Недостаточно прав для изминения имени на " + newNickName);
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

    public List<Member> getChannelPlayers(MessageCreateEvent event,
                                          String... excludeMembers) {
        return event.getMember().get()
                .getVoiceState().block()
                .getChannel().block()
                .getVoiceStates().map(st -> st.getMember().block())
                .filter(member -> Arrays.stream(excludeMembers)
                        .anyMatch(exc -> !getNickName(member).startsWith(exc))
                ).collectList().block();
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

    private void sendChat(MessageCreateEvent event, String s) {
        event.getMessage().getChannel().block().createMessage(s).block();
    }

    public Member getPlayerByStartsWithNick(List<Member> members, String nick) {
        return members.stream().filter(player -> getNickName(player).startsWith(nick)).findFirst().orElse(null);
    }
}
