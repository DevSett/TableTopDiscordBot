package ru.devsett.bot.service.receiver;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.RangService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Emoji;
import ru.devsett.bot.util.Role;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.config.DiscordConfig;
import ru.devsett.db.service.impl.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class MasterReceiverService {
    private final MessageService messageService;
    private final UserService userService;
    private final DiscordService discordService;
    private final RangService rangService;
    private final WinRateService winRateService;
    private final DiscordConfig discordConfig;
    private final GameHistoryService gameHistoryService;
    private final ChannelService channelService;

    @Getter
    private Member telegramMember;
    @Getter
    private String tokenTelegramSession;

    public MasterReceiverService(MessageService messageService, UserService userService, DiscordService discordService, RangService rangService, WinRateService winRateService, DiscordConfig discordConfig, GameHistoryService gameHistoryService, ChannelService channelService) {
        this.messageService = messageService;
        this.userService = userService;
        this.discordService = discordService;
        this.rangService = rangService;
        this.winRateService = winRateService;
        this.discordConfig = discordConfig;
        this.gameHistoryService = gameHistoryService;
        this.channelService = channelService;
    }

    @CommandName(names = {"игра"})
    public void historyGame(MessageReceivedEvent event, String command) {

        var cmd = command.split(" ");
        Integer num = Integer.valueOf(cmd[1]);

        var game = gameHistoryService.getGameById(num);

        discordService.sendChatEmbedTemp(event, "Игра №"+num, "Победа "+ (game.isWinRed()?"Красных":"Черных"),null);
    }

    @CommandName(names = {"clearch"})
    public void deleateAllTypeChannels(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        channelService.clearTypes();
    }

    @CommandName(names = {"clearh"})
    public void deleateAllHistoryDontStop(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        gameHistoryService.deleteAllStopGames();
    }

    @CommandName(names = {"создать-новости-адд"})
    public void createNewsAddChannel(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.NEWS_ADD_CHANNEL);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 15);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var chNewsEntity = channelService.findByType(TypeChannel.NEWS_CHANNEL);
                var chNewsAddEntity = channelService.findByType(TypeChannel.NEWS_ADD_CHANNEL);

                var chNews = MafiaBot.getGuild().getTextChannelById(chNewsEntity.getId());
                var chNewsAdd = MafiaBot.getGuild().getTextChannelById(chNewsAddEntity.getId());

                if (!chNewsAdd.hasLatestMessage()) {
                    return;
                }
                List<String> news = new ArrayList<>();

                chNewsAdd.getHistory().retrievePast(10).queue(messages -> {
                    for (Message message : messages) {
                        news.add(message.getContentRaw());
                        message.delete().queue();
                    }
                    chNews.sendMessage(String.join("\n", news)).queue();
                });
            }
        }, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        discordService.sendChat(event.getTextChannel(), "Чат новостей заплонирован!");
    }

    @CommandName(names = {"создать-новости"})
    public void createNewsChannel(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.NEWS_CHANNEL);
    }

    @CommandName(names = {"создать-вход"})
    public void createJoinChannel(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.JOIN_CHANNEL);
    }

    @CommandName(names = {"кто-бан"})
    public void whoBan(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }

        var cmd = command.split(" ");
        var user = userService.findById(Long.parseLong(cmd[1]));
        if (user != null) {
            discordService.sendChat(event.getTextChannel(), user.getUserName() + " забанен " + userService.findById(user.getWhoBan().getId()).getUserName());
        } else {
            discordService.sendChat(event.getTextChannel(), "Юзер не найден!");
        }
    }

    @CommandName(names = {"создать-бан"})
    public void createBanChannel(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.BAN_CHANNEL);
    }

    @CommandName(names = {"создать-мастер"})
    public void createMasterChannel(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        discordService.sendChat(event.getTextChannel(), "Создать классическую мафию", Emoji.GAME);
        discordService.sendChat(event.getTextChannel(), "Создать городскую мафию", Emoji.GAME);

        var ch = event.getTextChannel();

        var chE = channelService.getOrNewChannel(ch.getName(), ch.getIdLong(), false);
        channelService.updateType(chE, TypeChannel.MASTER_CHANNEL);
    }

    @CommandName(names = {"грязь"})
    public void getMessages(MessageReceivedEvent event, String command) {
        var nick = command.split(" ");
        if (discordService.isPresentRole(event, Role.MASTER) && nick.length > 1) {
            var name = nick[1];
            discordService.sendPrivateMessage(event.getMember(), messageService.getAllMessages(name));
        }
    }

    @CommandName(names = {"вер"})
    public void getVersion(MessageReceivedEvent event, String command) {
        discordService.sendChatEmbed(event, "Версия: " + discordConfig.getBuildVersion(),
                " Время сборки: " + discordConfig.getBuildTimestamp(), null);
    }

    @CommandName(names = {"хелп"})
    public void help(MessageReceivedEvent event, String command) {
        var msgHelp =
                "зр - выдать/забрать роль зрителя с перфиксом \"Зр.\"\n" +
                        "вд - выдать/забрать роль ведущего без префикса ! (только для ведущего и опытного)\n" +
                        "Только для ведущего:\n" +
                        "мут - мут всех в канале\n" +
                        "анмут - размут всех в канале\n" +
                        "ордер - выдать номера всем игрокам в комнате кроме роли Зр.\n" +
                        "бункер - начать игру бункер (выдача всем в лс их персонажей и состояния бункера с катастрофой)\n" +
                        "проф - выдача всем новых профессий\n" +
                        "проф %Начала ника игроков через запятую% - выдать профессии определенным игрокам\n" +
                        "доп - выдача всем новых доп. информаций\n" +
                        "доп %Начала ника игроков через запятую% - выдать доп. инфу определенным игрокам\n" +
                        "зд - выдача всем новых состояний здоровья \n" +
                        "зд %Начала ника игроков через запятую% - выдать здоровье определенным игрокам\n" +
                        "багаж- выдача всем новых багажей\n" +
                        "багаж %Начала ника игроков через запятую% - выдать багажа определенным игрокам\n" +
                        "телеграм %Любой токен% - токен для синхронизации с телеграмом\n" +
                        "черта- выдача всем новых черт\n" +
                        "черта %Начала ника игроков через запятую% - выдать черту определенным игрокам\n" +
                        "хобби- выдача всем новых  хобби \n" +
                        "хобби %Начала ника игроков через запятую% - выдать определенным игрокам хобби \n" +
                        "фобия- выдача всем новых фобий \n" +
                        "фобия %Начала ника игроков через запятую% - выдать определенным игрокам фобию \n" +
                        "персонаж- выдача всем новых персонажей\n" +
                        "персонаж %Начала ника игроков через запятую% - выдать персонажа определенным игрокам\n" +
                        "новый-бункер - выдача всем нового бункера\n" +
                        "новый-игрок %Начала ника игроков через запятую% - выдать нового игрокового персонажа с новыми характеристиками определенным игрокам\n" +
                        "катастрофа - выдача всем новой катастрофы\n" +
                        "грязь %ЮзерНейм игрока% - выдаст всю историю сообщений с игроком у бота (последние 2000 символов)\n" +
                        "телеграм %Любой номер% - токен для синхронизации с телеграмом";

        var rate = "коины - показывает ваш баланс\n" +
                "коины %ЮзерНейм% - показывает баланс игрока\n" +
                "богачи - показать лучших богачей\n" +
                "топ - показать топ по винрейту мафии\n" +
                "винрейт - показывает ваш винрейт по мафии\n" +
                "винрейт %ЮзерНейм% - показывает винрейт по мафии\n" +
                "отдать-коины %юзернейм% %кол-во%";
        discordService.sendChatEmbed(event, "Команды", msgHelp, "https://github.com/DevSett/TableTopDiscordBot");
        discordService.sendChatEmbed(event, "Деньги и винрейт", rate, null);
        if (discordService.isPresentRole(event, Role.MODERATOR)) {
            var msgHelp2 = "фастбан %Начала никнейма который сидит в вашем войсе% %Причина% %Кол-во часов%\n"
                    + "бан %юзернейм% %Причина% %Кол-во часов%\n"
                    + "анбан %юзернейм%\n"
                    + "адд-коины %юзернейм% %кол-во%\n"
                    + "хайдбан %юзернейм% %кол-в часово%\n"
                    + "хайдфастбан %Начала никнейма который сидит в вашем войсе% %Кол-во часов%\n"
                    + "в-красные %теги игроков через пробел% - зачисление статистики красным\n"
                    + "в-черные %теги игроков через пробел% - зачисление статистики черным\n"
                    + "п-красные %теги игроков через пробел% - зачисление статистики красным\n"
                    + "п-черные %теги игроков через пробел% - зачисление статистики черным\n"
                    + "в-дон %теги игроков через пробел% - зачисление статистики дону\n"
                    + "п-дон %теги игроков через пробел% - зачисление статистики дону\n"
                    + "п-шериф %теги игроков через пробел% - зачисление статистики шерифу\n"
                    + "в-ход %теги игроков через запятую% %кол-во% - зачисление статистики дону\n"
                    + "в-ведущий %теги игроков через пробел%"
                    + "в-шериф %теги игроков через пробел% - зачисление статистики шерифу\n"
                    + "п-ход %теги игроков через запятую% %кол-во% - зачисление статистики дону";

            discordService.sendChatEmbed(event, "ДЛЯ МОДЕРАТОРОВ", msgHelp2, "https://github.com/DevSett/TableTopDiscordBot");
        }
    }

    @CommandName(names = {"п-ход"})
    public void addMiss(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        var spl = command.split(" ");
        if (spl.length < 3) {
            throw new DiscordException("Некорректно введена команда!");
        }
        var players = spl[1].split(",");

        for (String player : players) {
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = winRateService.addMiss(user, number);
                discordService.sendChatEmbed(event, "Кол-во не угаданных мафий у " + user.getUserName(), rate.getMafiaMiss() + "", null);
            }
        }
    }

    @CommandName(names = {"в-ведущий"})
    public void addMaster(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addMaster(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за дона " + user.getUserName(), rate.getMafiaMaster() + "", null);
            }
        }

    }


    @CommandName(names = {"в-ход"})
    public void addBest(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        var spl = command.split(" ");
        if (spl.length < 3) {
            throw new DiscordException("Некорректно введена команда!");
        }
        var players = spl[1].split(",");

        for (String player : players) {
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = winRateService.addFind(user, number);
                discordService.sendChatEmbed(event, "Кол-во угаданных мафий у " + user.getUserName(), rate.getMafiaFind() + "", null);
            }
        }
    }

    @CommandName(names = {"в-дон"})
    public void addDonWinRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addDonWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за дона " + user.getUserName(), rate.getMafiaWinDon() + "", null);
            }
        }

    }

    @CommandName(names = {"п-дон"})
    public void addDonLoseRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addDonLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за дона " + user.getUserName(), rate.getMafiaLoseDon() + "", null);
            }
        }
    }

    @CommandName(names = {"в-шериф"})
    public void addSheriffWinRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addSheriffWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за шерифа " + user.getUserName(), rate.getMafiaWinSheriff() + "", null);
            }
        }
    }

    @CommandName(names = {"п-шериф"})
    public void addSheriffLoseRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addSheriffLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за шерифа " + user.getUserName(), rate.getMafiaLoseSheriff() + "", null);
            }
        }
    }

    @CommandName(names = {"в-красные"})
    public void addRedWinRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addRedWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за красных " + user.getUserName(), rate.getMafiaWinRed() + "", null);
            }
        }
    }

    @CommandName(names = {"п-красные"})
    public void addRedLoseRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addRedLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за красных " + user.getUserName(), rate.getMafiaLoseRed() + "", null);
            }
        }
    }

    @CommandName(names = {"в-черные"})
    public void addRedWinBlack(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addBlackWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за черных " + user.getUserName(), rate.getMafiaWinBlack() + "", null);
            }
        }
    }

    @CommandName(names = {"п-черные"})
    public void addRedLoseBlack(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MASTER)) {
            return;
        }

        var spl = command.split(" ");
        if (spl.length < 2) {
            throw new DiscordException("Некорректно введена команда!");
        }
        for (int i = 1; i < spl.length; i++) {
            var player = spl[i];
            var id = getId(player);
            var user = userService.findById(id);
            if (user == null) {
                var member = MafiaBot.getGuild().getMemberById(id);
                if (member != null) {
                    user = userService.getOrNewUser(member);
                }
            }

            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addBlackLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за черных " + user.getUserName(), rate.getMafiaLoseBlack() + "", null);
            }
        }
    }

    @CommandName(names = {"топ"})
    public void top(MessageReceivedEvent event, String command) {
        discordService.sendChatEmbedTemp(event, "Топ игроков в мафию по городу",
                null, null, rangService.getTopWinRate(), 30);
    }

    @CommandName(names = {"топК"})
    public void topK(MessageReceivedEvent event, String command) {
        discordService.sendChatEmbedTemp(event, "Топ игроков в мафию по классике",
                null, null, rangService.getTopWinRateK(), 30);
    }

    @CommandName(names = {"винрейт"})
    public void winrate(MessageReceivedEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbedTemp(event, "Ваш винрейт город",
                    null, null, rangService.getWinRate(userService.getOrNewUser(event.getMember())),30);
        } else {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChatTemp(event.getTextChannel(), "Пользователь не найден!", 30);
            } else {
                discordService.sendChatEmbedTemp(event, "Винрейт города " + user.getUserName(), null, null, rangService.getWinRate(user),30);
            }
        }
    }

    @CommandName(names = {"винрейтк"})
    public void winrateK(MessageReceivedEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbedTemp(event, "Ваш винрейт классика",
                    null, null, rangService.getWinRateK(userService.getOrNewUser(event.getMember())),30);
        } else {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                discordService.sendChatEmbedTemp(event, "Винрейт классики " + user.getUserName(), null, null, rangService.getWinRateK(user),30);
            }
        }
    }

    @CommandName(names = {"коины"})
    public void rate(MessageReceivedEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbedTemp(event, "Ваш баланс",
                    userService.getOrNewUser(event.getMember()).getRating() + "", null);
        } else {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChatTemp(event.getTextChannel(), "Пользователь не найден!",30);
            } else {
                discordService.sendChatEmbedTemp(event, "баланс " + user.getUserName(), user.getRating() + "", null);
            }
        }
    }

    private long getId(String s) {
        try {
            return Long.parseLong(s.substring(3, s.length() - 1));
        } catch (Exception e) {
            throw new DiscordException("Не верно выбран пользователь, нужно указать ссылку(тег) через @");
        }
    }

    @CommandName(names = {"богачи"})
    public void topMoney(MessageReceivedEvent event, String command) {
        discordService.sendChatEmbedTemp(event, "10 самых богатых игроков",
                null, null, userService.getTopMoney(), 30);
    }

    @CommandName(names = {"отдать-коины"})
    public void gaveMoney(MessageReceivedEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length < 3) {
            return;
        }
        var user = userService.findById(getId(spl[1]));
        if (user == null) {
            discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
        } else {
            var number = getRate(spl, 2);
            var godUser = userService.getOrNewUser(event.getMember());
            if (godUser.getRating() < number) {
                discordService.sendChat(event.getTextChannel(), "Недостаточно средств!");
                return;
            }
            var from = "<@!" + event.getMember().getIdLong() + ">";
            var sendedRate = userService.addRating(user, number,
                    from, discordService);
            var fallRate = userService.addRating(godUser, -1 * number, from, discordService);

            discordService.sendChatEmbed(event, ":moneybag: Баланс " + sendedRate.getUserName(), sendedRate.getRating() + "", null);
            discordService.sendChatEmbed(event, ":moneybag: Баланс " + fallRate.getUserName(), fallRate.getRating() + "", null);
        }
    }

    @CommandName(names = {"адд-коины"})
    public void addRate(MessageReceivedEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        var spl = command.split(" ");
        if (spl.length == 2) {
            var number = getRate(spl, 1);
            var user = userService.getOrNewUser(event.getMember());
            var newRate = userService.addRating(user, number,
                    "<@!" + event.getMember().getIdLong() + ">", discordService).getRating();
            discordService.sendChatEmbed(event, "Ваш баланс", ":moneybag: " + newRate + "", null);
        } else if (spl.length > 2) {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChat(event.getTextChannel(), "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = userService.addRating(user, number,
                        "<@!" + event.getMember().getIdLong() + ">", discordService).getRating();
                discordService.sendChatEmbed(event, "Баланс " + user.getUserName(), ":moneybag: " + rate + "", null);
            }
        }
    }

    private Integer getRate(String[] spl, Integer num) {
        Integer number = 0;
        try {
            number = Integer.valueOf(spl[num]);
        } catch (Exception e) {
            throw new DiscordException("Введите кол-во рейтинга!");
        }
        return number;
    }

    @CommandName(names = {"телеграм"})
    public void telegram(MessageReceivedEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            this.tokenTelegramSession = command.split(" ")[1];
            this.telegramMember = event.getMember();
        }
    }

    @CommandName(names = {"мут"})
    public void muteall(MessageReceivedEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.muteall(event);
        }
    }

    @CommandName(names = {"анмут"})
    public void unmuteall(MessageReceivedEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.unmuteall(event);
        }
    }

//    @CommandName(names = {"зр", "зритель", "смотреть", "watch", "watcher"})
//    public void watcher(MessageReceivedEvent event, String command) {
//        discordService.addOrRemoveRole(event, Role.WATCHER);
//        discordService.changeNickName(event.getMember(), nickName -> !nickName.startsWith("Зр.") ? "Зр." + nickName : nickName);
//    }
//
//    @CommandName(names = {"ведущий", "вд"})
//    public void master(MessageReceivedEvent event, String command) {
//        if (discordService.isPresentRole(event, Role.EXPERT, Role.MASTER)) {
//            var action = discordService.addOrRemoveRole(event, Role.MASTER);
//        }
//    }

    @CommandName(names = {"ордер"})
    public void playMafia(MessageReceivedEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.randomOrderPlayers(event, discordService.getChannelPlayers(event, "Зр."));
        }
    }
}
