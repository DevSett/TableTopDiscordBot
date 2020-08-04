package ru.devsett.bot.service.receiver;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.RangService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;
import ru.devsett.db.service.WinRateService;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class MasterReceiverService {
    private final MessageService messageService;
    private final UserService userService;
    private final DiscordService discordService;
    private final RangService rangService;
    private final WinRateService winRateService;

    @Getter
    private Member telegramMember;
    @Getter
    private String tokenTelegramSession;

    public MasterReceiverService(MessageService messageService, UserService userService, DiscordService discordService, RangService rangService, WinRateService winRateService) {
        this.messageService = messageService;
        this.userService = userService;
        this.discordService = discordService;
        this.rangService = rangService;
        this.winRateService = winRateService;
    }

    @CommandName(names = {"грязь"})
    public void getMessages(MessageCreateEvent event, String command) {
        var nick = command.split(" ");
        if (discordService.isPresentRole(event, Role.MASTER) && nick.length > 1) {
            var name = nick[1];
            discordService.sendPrivateMessage(event, event.getMember().get(), messageService.getAllMessages(name));
        }
    }


    @CommandName(names = {"хелп"})
    public void help(MessageCreateEvent event, String command) {
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
                        "телеграм %Любой номер% - токен для синхронизации с телеграмом\n" +
                        "\nMADE BY KillSett v 0.28";

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
    public void addMiss(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = winRateService.addMiss(user, number);
                discordService.sendChatEmbed(event, "Кол-во не угаданных мафий у " + user.getUserName(), rate.getMafiaMiss() + "", null);
            }
        }
    }
    @CommandName(names = {"в-ведущий"})
    public void addMaster(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addMaster(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за дона " + user.getUserName(), rate.getMafiaMaster() + "", null);
            }
        }

    }


    @CommandName(names = {"в-ход"})
    public void addBest(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = winRateService.addFind(user, number);
                discordService.sendChatEmbed(event, "Кол-во угаданных мафий у " + user.getUserName(), rate.getMafiaFind() + "", null);
            }
        }
    }

    @CommandName(names = {"в-дон"})
    public void addDonWinRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addDonWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за дона " + user.getUserName(), rate.getMafiaWinDon() + "", null);
            }
        }

    }

    @CommandName(names = {"п-дон"})
    public void addDonLoseRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addDonLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за дона " + user.getUserName(), rate.getMafiaLoseDon() + "", null);
            }
        }
    }

    @CommandName(names = {"в-шериф"})
    public void addSheriffWinRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addSheriffWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за шерифа " + user.getUserName(), rate.getMafiaWinSheriff() + "", null);
            }
        }
    }

    @CommandName(names = {"п-шериф"})
    public void addSheriffLoseRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addSheriffLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за шерифа " + user.getUserName(), rate.getMafiaLoseSheriff() + "", null);
            }
        }
    }

    @CommandName(names = {"в-красные"})
    public void addRedWinRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addRedWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за красных " + user.getUserName(), rate.getMafiaWinRed() + "", null);
            }
        }
    }

    @CommandName(names = {"п-красные"})
    public void addRedLoseRate(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addRedLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за красных " + user.getUserName(), rate.getMafiaLoseRed() + "", null);
            }
        }
    }

    @CommandName(names = {"в-черные"})
    public void addRedWinBlack(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addBlackWin(user);
                discordService.sendChatEmbed(event, "Кол-во выйгранных игр за черных " + user.getUserName(), rate.getMafiaWinBlack() + "", null);
            }
        }
    }

    @CommandName(names = {"п-черные"})
    public void addRedLoseBlack(MessageCreateEvent event, String command) {
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
                var member = MafiaBot.getGuild().getMemberById(Snowflake.of(id)).blockOptional();
                if (member.isPresent()) {
                    user = userService.getOrNewUser(member.get());
                }
            }

            if (user == null) {
                discordService.sendChat(event, "Пользователь " + player + " не найден!");
            } else {
                var rate = winRateService.addBlackLose(user);
                discordService.sendChatEmbed(event, "Кол-во проигранных игр за черных " + user.getUserName(), rate.getMafiaLoseBlack() + "", null);
            }
        }
    }

    @CommandName(names = {"топ"})
    public void top(MessageCreateEvent event, String command) {
        discordService.sendChatEmbed(event, "Топ игроков в мафию",
                null, null,rangService.getTopWinRate());
    }


    @CommandName(names = {"винрейт"})
    public void winrate(MessageCreateEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbed(event, "Ваш винрейт",
                    null, null, rangService.getWinRate(userService.getOrNewUser(event.getMember().get())));
        } else {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                discordService.sendChatEmbed(event, "Винрейт " + user.getUserName(), null, null,rangService.getWinRate(user));
            }
        }
    }

    @CommandName(names = {"хайдбан"})
    public void hideban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.hideban(event, spl[1], 720);
            }
            if (spl.length == 3) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[2]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.hideban(event, spl[1], number);
            }
        }
    }

    @CommandName(names = {"хайдфастбан"})
    public void hidefastban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.hidefastban(event, spl[1], 720);
            }

            if (spl.length == 3) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[2]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.hidefastban(event, spl[1], number);
            }
        }
    }

    @CommandName(names = {"фастбан"})
    public void fastban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 3) {
                discordService.fastban(event, spl[1], spl[2], 720);
            }
            if (spl.length == 4) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[3]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.fastban(event, spl[1], spl[2], number);
            }
        }
    }

    @CommandName(names = {"бан"})
    public void ban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 3) {
                discordService.ban(event, spl[1], spl[2], 720);
            }
            if (spl.length == 4) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[3]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.ban(event, spl[1], spl[2], number);
            }
        }
    }

    @CommandName(names = {"анбан"})
    public void unban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.unban(event, spl[1]);
            }
        }
    }

    @CommandName(names = {"коины"})
    public void raite(MessageCreateEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbed(event, "Ваш баланс",
                    userService.getOrNewUser(event.getMember().get()).getRating() + "", null);
        } else {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                discordService.sendChatEmbed(event, "баланс " + user.getUserName(), user.getRating() + "", null);
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
    public void topMoney(MessageCreateEvent event, String command) {
        discordService.sendChatEmbed(event, "10 самых богатых игроков",
                null, null, userService.getTopMoney());
    }

    @CommandName(names = {"отдать-коины"})
    public void gaveMoney(MessageCreateEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length < 3) {
            return;
        }
        var user = userService.findById(getId(spl[1]));
        if (user == null) {
            discordService.sendChat(event, "Пользователь не найден!");
        } else {
            var number = getRate(spl, 2);
            var godUser = userService.getOrNewUser(event.getMember().get());
            if (godUser.getRating() < number) {
                discordService.sendChat(event, "Недостаточно средств!");
                return;
            }
            var from = "<@!" + event.getMember().get().getId().asLong() + ">";
            var sendedRate = userService.addRating(user, number,
                    from, discordService);
            var fallRate = userService.addRating(godUser, -1 * number, from, discordService);

            discordService.sendChatEmbed(event, ":moneybag: Баланс " + sendedRate.getUserName(), sendedRate.getRating() + "", null);
            discordService.sendChatEmbed(event, ":moneybag: Баланс " + fallRate.getUserName(), fallRate.getRating() + "", null);
        }
    }

    @CommandName(names = {"адд-коины"})
    public void addRaite(MessageCreateEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        var spl = command.split(" ");
        if (spl.length == 2) {
            var number = getRate(spl, 1);
            var user = userService.getOrNewUser(event.getMember().get());
            var newRate = userService.addRating(user, number,
                    "<@!" + event.getMember().get().getId().asLong() + ">", discordService).getRating();
            discordService.sendChatEmbed(event, "Ваш баланс", ":moneybag: " + newRate + "", null);
        } else if (spl.length > 2) {
            var user = userService.findById(getId(spl[1]));
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                var number = getRate(spl, 2);
                var rate = userService.addRating(user, number,
                        "<@!" + event.getMember().get().getId().asLong() + ">", discordService).getRating();
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
    public void telegram(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            this.tokenTelegramSession = command.split(" ")[1];
            this.telegramMember = event.getMember().get();
        }
    }

    @CommandName(names = {"мут"})
    public void muteall(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.muteall(event);
        }
    }

    @CommandName(names = {"анмут"})
    public void unmuteall(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.unmuteall(event);
        }
    }

    @CommandName(names = {"зр", "зритель", "смотреть", "watch", "watcher"})
    public void watcher(MessageCreateEvent event, String command) {
        discordService.addOrRemoveRole(event, Role.WATCHER);
        discordService.changeNickName(event, event.getMember().get(), nickName -> !nickName.startsWith("Зр.") ? "Зр." + nickName : nickName);
    }

    @CommandName(names = {"ведущий", "вд"})
    public void master(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.EXPERT, Role.MASTER)) {
            var action = discordService.addOrRemoveRole(event, Role.MASTER);
        }
    }

    @CommandName(names = {"ордер"})
    public void playMafia(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.randomOrderPlayers(event, discordService.getChannelPlayers(event, "Зр."));
        }
    }

    public void checkOnBan() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            userService.getUsersForUnBan().forEach(user -> {
                try {
                    MafiaBot.getGuild().unban(Snowflake.of(user.getId())).block();
                    try {
                        discordService.addOrRemoveRole(MafiaBot.getGuild(),
                                Optional.ofNullable(MafiaBot.getGuild().getMemberById(Snowflake.of(user.getId())).block()),
                                Role.BAN);
                    } catch (Exception e) {
                        log.error(e);
                    }
                    userService.unban(user);
                } catch (Exception e) {
                    log.error(e);
                }
            });
        }, 0, 1, TimeUnit.HOURS);
    }
}
