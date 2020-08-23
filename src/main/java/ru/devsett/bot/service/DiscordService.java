package ru.devsett.bot.service;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.intefaces.NickNameEvent;
import ru.devsett.bot.service.games.MafiaService;
import ru.devsett.bot.service.receiver.MessageReceiverService;
import ru.devsett.bot.util.*;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;

import java.awt.*;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
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

    public ActionDo addOrRemoveRole(MessageReceivedEvent event, Role role) {
        return addOrRemoveRole(event.getGuild(), event.getMember(), role);
    }

    public ActionDo addOrRemoveRole(Guild guild, Member member, Role role) {
        if (member == null) {
            return ActionDo.NOTHING;
        }

        var findedRole = guild.getRoles().stream()
                .filter(roleDiscord -> roleDiscord.getName().equals(role.getName()))
                .findFirst();

        if (findedRole.isEmpty()) {
            return ActionDo.NOTHING;
        }

        var quildRole = findedRole.get();

        var isPresentRole = member.getRoles().stream()
                .anyMatch(roleDiscord -> quildRole.getId().equals(roleDiscord.getId()));
        if (isPresentRole) {
            guild.removeRoleFromMember(member, quildRole).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            return ActionDo.REMOVE;
        } else {
            guild.addRoleToMember(member, quildRole).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            return ActionDo.ADD;
        }
    }

    public String changeNickName(MessageReceivedEvent event, Member member, NickNameEvent nickNameEvent) {
        var newNickName = nickNameEvent.getName(getNickName(member));
        try {
            member.modifyNickname(newNickName).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            return newNickName;
        } catch (HierarchyException e) {
            sendChat(event, "Недостаточно прав для изминения имени на " + newNickName);
        }
        return "";
    }

    public Boolean isPresentRole(MessageReceivedEvent event, Role... roles) {
        if (roles.length == 0) {
            return false;
        }
        return event.getMember()
                .getRoles()
                .stream().anyMatch(roleDiscord -> Arrays.stream(roles)
                        .anyMatch(role -> role.getName().equals(roleDiscord.getName())));
    }

    public Boolean isPresentPermission(MessageReceivedEvent event, Role role, Permission... permissions) {
        if (permissions.length == 0) {
            return true;
        }

        return event.getMember()
                .getRoles()
                .stream()
                .filter(roleDiscord -> role.getName().equals(roleDiscord.getName()))
                .findFirst().orElseThrow(() -> new DiscordException("Роль не найдена!"))
                .getPermissions().stream().anyMatch(perm -> Arrays.asList(permissions).contains(perm));
    }

    public List<Member> getChannelPlayers(MessageReceivedEvent event,
                                          String... excludeMembers) {
        return getChannelPlayers(getChannel(event), excludeMembers);
    }

    public List<Member> getChannelPlayers(VoiceChannel channel,
                                          String... excludeMembers) {
        return channel.getMembers().stream()
                .filter(member -> excludeMembers.length <= 0 || Arrays.stream(excludeMembers)
                        .anyMatch(exc -> !getNickName(member).startsWith(exc))
                ).collect(Collectors.toList());
    }

    public VoiceChannel getChannel(MessageReceivedEvent event) {
        try {
            return Optional.ofNullable(event.getMember())
                    .orElse(MafiaBot.getGuild().getMember(event.getAuthor()))
                    .getVoiceState().getChannel();
        } catch (NullPointerException e) {
            throw new DiscordException("Войс канал не найден или недостаточно прав!");
        }
    }

    public void randomOrderPlayers(MessageReceivedEvent MessageReceivedEvent, List<Member> channelPlayers) {
        var members = new ArrayList<>(channelPlayers);
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
                newNickName = changeNickName(MessageReceivedEvent, member, name -> name.substring(3));
            }
            String finalNewNickName = newNickName;
            changeNickName(MessageReceivedEvent, member, name -> numberString(finalNumber) + (finalNewNickName.isEmpty() ? name : finalNewNickName));
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
        return Optional.ofNullable(member.getNickname()).orElse(member.getUser().getName());
    }

    public void sendPrivateMessage(MessageReceivedEvent event, Member member, String msg) {
        if (msg.length() > 1999) {
            msg = msg.substring(msg.length() - 1999);
        }
        String finalMsg = msg;
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(finalMsg).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    }));
        messageService.sendMessage(member, msg);
    }
    public void sendChat(MessageReceivedEvent event, String msg, Emoji... emojis) {
        event.getChannel().sendMessage(msg).queue(message -> {
            if (emojis != null && emojis.length > 0) {
                for (Emoji emoji : emojis) {
                    message.addReaction(emoji.getName())
                            .queue(null, error -> {
                                toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
                }
            }
        });
    }

    public void sendChat(MessageReceivedEvent event, String msg) {
        event.getChannel().sendMessage(msg).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
    }

    public Member getPlayerByStartsWithNick(List<Member> members, String nick) {
        return members.stream().filter(player -> getNickName(player).startsWith(nick)).findFirst()
                .orElseThrow(() -> new DiscordException("Не найден никнейм " + nick));
    }

    public void unmuteall(MessageReceivedEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            player.mute(false).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
        });
    }

    public void muteall(MessageReceivedEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            if (player != telegramSession.getMember()) {
                player.mute(true).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            }
        });
    }

    public void muteall(Member member) {
        getChannelPlayers(member.getVoiceState().getChannel()).forEach(player -> {
            if (player != member) {
                player.mute(true).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            }
        });
    }

    public void unmuteall(Member member) {
        getChannelPlayers(member.getVoiceState().getChannel()).forEach(player -> {
            if (player != member) {
                player.mute(false).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
            }
        });
    }

    public void sendChatEmbed(MessageReceivedEvent event, String title, String msgHelp, String url) {
        sendChatEmbed(event, title, msgHelp, url, Collections.emptyList());
    }

    public void sendChatEmbed(MessageReceivedEvent event, String title, String msgHelp, String url, List<Field> fields) {
        var embBuilder = new EmbedBuilder().setTitle(title, url).setDescription(msgHelp);
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                embBuilder.addField(field.getName(), field.getValue(), field.isInline());
            }
        }
        event.getMessage().getChannel().sendMessage(embBuilder.build()).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
    }

    public void toLogTextChannel(String title, String description, MessageReceivedEvent event, int color) {
        var name = "Неизвестно";
        var currentName = "Неизвестно";

        try {
            var member = event.getMember();
            name = member != null ? member.getEffectiveName() : name;
            var current = event.getMessage().getChannel();
            if (current instanceof TextChannel) {
                currentName = ((TextChannel) current).getName();
            }
        } catch (Exception ignored) {
        }
        var footer = "Юзер: " + name + ", Канал: " + currentName;
        toLog(title, footer, description, color);

    }

    public void toLogVoiceChannel(String title, String description, Member member, VoiceChannel channel, int color) {
        var name = "Неизвестно";
        var currentName = "Неизвестно";

        try {
            name = member != null ? member.getEffectiveName() : name;
            currentName = channel != null ? channel.getName() : currentName;
        } catch (Exception ignored) {
        }
        var footer = "Юзер: " + name + ", Канал: " + currentName;

        toLog(title, footer, description, color);
    }

    public void toLog(String title, String footer, String description, int color) {
        try {
            var channel = MafiaBot.getGuild()
                    .getTextChannelsByName("log", true).stream().findFirst();

            if (channel.isEmpty()) {
                return;
            }

            var textChannel = channel.get();
            var builder = new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .setFooter(footer)
                    .setColor(color);

            textChannel.sendMessage(builder.build()).queue(null, error -> {
                        toLog("Exception", null, error.getMessage(), Color.RED.getRGB());
                    });
        } catch (Exception e2) {
            log.error(e2);
        }
    }
}
