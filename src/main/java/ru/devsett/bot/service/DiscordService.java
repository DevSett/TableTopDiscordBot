package ru.devsett.bot.service;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.NickNameEvent;
import ru.devsett.bot.util.*;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.ChannelService;
import ru.devsett.db.service.impl.ConfigService;
import ru.devsett.db.service.impl.MessageService;
import ru.devsett.db.service.impl.UserService;

import java.awt.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DiscordService {

    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;
    private final ConfigService configService;

    public DiscordService(MessageService messageService, UserService userService, ChannelService channelService, ConfigService configService) {
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
        this.configService = configService;
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
                toLog(guild,"Exception", null, error.getMessage(), Color.RED.getRGB());
            });
            return ActionDo.REMOVE;
        } else {
            guild.addRoleToMember(member, quildRole).queue(null, error -> {
                toLog(guild,"Exception", null, error.getMessage(), Color.RED.getRGB());
            });
            return ActionDo.ADD;
        }
    }

    public String changeNickName(Member member, NickNameEvent nickNameEvent) {
        var newNickName = nickNameEvent.getName(getNickName(member));
        try {
            member.modifyNickname(newNickName).queue();
        } catch (HierarchyException ex) {
            toLog(member.getGuild(),"Exception", null, ex.getMessage(), Color.RED.getRGB());
        }
        return newNickName;
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
                    .orElse(event.getGuild().getMember(event.getAuthor()))
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
                newNickName = changeNickName(member, name -> name.substring(3));
            }
            String finalNewNickName = newNickName;
            changeNickName(member, name -> numberString(finalNumber) + (finalNewNickName.isEmpty() ? name : finalNewNickName));
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

    public void sendPrivateMessageEmbed(Member member, String msg) {
        if (msg.length() > 1999) {
            msg = msg.substring(msg.length() - 1999);
        }
        String finalMsg = msg;
        member.getUser().openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(new EmbedBuilder().setDescription(finalMsg).build())
                        .queue(null, error -> {
                            toLog(member.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
                        }));
        messageService.sendMessage(member, msg);
    }

    public void sendPrivateMessage(Member member, String msg) {
        if (msg.length() > 1999) {
            msg = msg.substring(msg.length() - 1999);
        }
        String finalMsg = msg;
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(finalMsg).queue(null, error -> {
            toLog(member.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
        }));
        messageService.sendMessage(member, msg);
    }

    public void sendChat(TextChannel channel, String msg, Emoji... emojis) {
        channel.sendMessage(msg).queue(message -> {
            if (emojis != null && emojis.length > 0) {
                for (Emoji emoji : emojis) {
                    message.addReaction(emoji.getName())
                            .queue(null, error -> {
                                toLog(channel.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
                            });
                }
            }
        });
    }

    public void sendChat(TextChannel channel, String msg) {
        channel.sendMessage(msg).queue(null, error -> {
            toLog(channel.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
        });
    }

    public void sendChatTemp(TextChannel channel, String msg, Integer seconds) {
        channel.sendMessage(msg).queue(message -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    message.delete().queue();
                    timer.cancel();
                }
            }, seconds * 1000, 1);
        }, error -> {
            toLog(channel.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
        });
    }

    public Member getPlayerByStartsWithNick(List<Member> members, String nick) {
        return members.stream().filter(player -> getNickName(player).startsWith(nick)).findFirst()
                .orElseThrow(() -> new DiscordException("Не найден никнейм " + nick));
    }

    public void unmuteall(MessageReceivedEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            player.mute(false).queue(null, error -> {
                toLog(telegramSession.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
            });
        });
    }

    public void muteall(MessageReceivedEvent telegramSession) {
        getChannelPlayers(telegramSession).forEach(player -> {
            if (player != telegramSession.getMember()) {
                player.mute(true).queue(null, error -> {
                    toLog(telegramSession.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
                });
            }
        });
    }

    public void muteall(Member member) {
        getChannelPlayers(member.getVoiceState().getChannel()).forEach(player -> {
            if (player != member) {
                player.mute(true).queue(null, error -> {
                    toLog(member.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
                });
            }
        });
    }

    public void unmuteall(Member member) {
        getChannelPlayers(member.getVoiceState().getChannel()).forEach(player -> {
            if (player != member) {
                player.mute(false).queue(null, error -> {
                    toLog(member.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
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
            toLog(event.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
        });
    }

    public void sendChatEmbedTemp(MessageReceivedEvent event, String title, String msgHelp, String url) {
        sendChatEmbedTemp(event, title, msgHelp, url, Collections.emptyList(), 30);
    }

    public void sendChatEmbedTemp(MessageReceivedEvent event, String title, String msgHelp, String url, List<Field> fields, Integer seconds) {
        var embBuilder = new EmbedBuilder().setTitle(title, url).setDescription(msgHelp);
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                embBuilder.addField(field.getName(), field.getValue(), field.isInline());
            }
        }
        event.getMessage().getChannel().sendMessage(embBuilder.build()).queue(msg -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                    event.getMessage().delete().queue();
                    timer.cancel();
                }
            }, seconds * 1000, 1);
        }, error -> {

            toLog(event.getGuild(), "Exception", null, error.getMessage(), Color.RED.getRGB());
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
        toLog(event.getGuild(), title, footer, description, color);

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

        toLog(channel.getGuild(), title, footer, description, color);
    }

    public void toLog(Guild guild, String title, String footer, String description, int color) {
        try {
            var channel = guild
                    .getTextChannelsByName("mylog", true).stream().findFirst();

            if (channel.isEmpty()) {
                return;
            }

            var textChannel = channel.get();
            var builder = new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(description)
                    .setFooter(footer)
                    .setColor(color);

            textChannel.sendMessage(builder.build()).queue();
        } catch (Exception e2) {
            log.error(e2);
        }
    }

    @SneakyThrows
    public List<Player> randomMafiaGame(MessageReactionAddEvent event) {
        var memberMsg = event.getMember();
        var channel = memberMsg.getVoiceState().getChannel();
        List<Member> membersOrdered = new ArrayList<>();

        for (Member channelMember : channel.getMembers()) {
            var retrivedMember = event.getGuild().retrieveMember(channelMember.getUser()).submit().get();
            var iffed = configService.isEnableWebCam()
                    ? retrivedMember.getVoiceState() != null && !retrivedMember.getVoiceState().isGuildMuted()
                    : !retrivedMember.getEffectiveName().toLowerCase().startsWith("зр.")
                    && !retrivedMember.getEffectiveName().startsWith("!");

            if (iffed) {
                if (retrivedMember.getIdLong() != memberMsg.getIdLong()) {
                    membersOrdered.add(retrivedMember);
                }
            }
        }

        List<Integer> membersNumbers = new ArrayList<>();
        List<MafiaRole> roles = new ArrayList<>();
        if (membersOrdered.size() < 10) {
            for (int i = 0; i < 5; i++) {
                roles.add(MafiaRole.RED);
            }
            for (int i = 0; i < 1; i++) {
                roles.add(MafiaRole.BLACK);
            }
        } else if (membersOrdered.size() < 12) {
            for (int i = 0; i < 6; i++) {
                roles.add(MafiaRole.RED);
            }
            for (int i = 0; i < 2; i++) {
                roles.add(MafiaRole.BLACK);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                roles.add(MafiaRole.RED);
            }
            for (int i = 0; i < 3; i++) {
                roles.add(MafiaRole.BLACK);
            }
        }

        roles.add(MafiaRole.SHERIFF);
        roles.add(MafiaRole.DON);
        var random = new SecureRandom();
        List<Player> players = new ArrayList<>();
        for (Member member : membersOrdered) {
            var number = random.nextInt(membersOrdered.size()) + 1;
            while (membersNumbers.contains(number)) {
                number = random.nextInt(membersOrdered.size()) + 1;
            }
            membersNumbers.add(number);
            int finalNumber = number;

            var nickName = getNickName(member);
            var newNickName = "";
            if (nickName.length() > 3 && nickName.toCharArray()[2] == '.' && isOrder(nickName.substring(0, 2))) {
                newNickName = changeNickName(member, name -> name.substring(3));
            }
            String finalNewNickName = newNickName;
            changeNickName(member, name -> numberString(finalNumber) + (finalNewNickName.isEmpty() ? name : finalNewNickName));
            var roleIndex = roles.size() != 0 ? random.nextInt(roles.size()) : 0;
            players.add(new Player(userService.getOrNewUser(member), roles.size() == 0 ? MafiaRole.RED : roles.get(roleIndex), finalNumber));
            players.sort((o1, o2) -> o1.getNumber() > o2.getNumber() ? 1 : -1);
            if (roles.size() != 0) {
                roles.remove(roleIndex);
            }
        }
        return players;
    }

    @SneakyThrows
    public void deleteOrder(MessageReactionAddEvent event) {
        var channels = event.getMember().getVoiceState().getChannel();
        for (Member member : channels.getMembers()) {
            var retrivedMember = event.getGuild().retrieveMember(member.getUser()).submit().get();
            var nickName = retrivedMember.getEffectiveName();
            if (nickName.length() > 3 && nickName.toCharArray()[2] == '.' && isOrder(nickName.substring(0, 2))) {
                changeNickName(retrivedMember, name -> name.substring(3));
            } else {
                changeNickName(retrivedMember, name -> name);
            }
        }
    }

    public void workTypeChannel(MessageReceivedEvent event) {
        var ch = event.getTextChannel();
        var chEntity = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        switch (chEntity.getTypeChannel()) {
            case BAN_CHANNEL:
                banChannel(event.getMessage(), event.getMember());
                break;
            case ROLE_CHANNEL:
                roleChannel();
                break;
            default:
                return;
        }
    }

    private void roleChannel() {
    }

    private void banChannel(Message message, Member member) {
        var content = message.getContentRaw();
        if (!content.contains("<") || !content.contains(">")) {
            return;
        }
        content = content.substring(content.indexOf("<") + 3, content.indexOf(">"));

        message.getGuild().retrieveMemberById(content).queue(findedMember -> {
            if (findedMember != null) {
                userService.getOrNewUser(findedMember);
                userService.ban(findedMember, member, 999);
                findedMember.ban(7, "Бан!").queue();
            }
        });

    }

    public void sendToJoinRoom(String s, Guild guild) {
        for (TextChannel textChannel : guild.getTextChannelsByName("⡇\uD83D\uDCEAпроходная", true)) {
            var builder = new EmbedBuilder()
                    .setTitle("Приглашение")
                    .setDescription(s);

            textChannel.sendMessage(builder.build()).queue();
        }
    }
}
