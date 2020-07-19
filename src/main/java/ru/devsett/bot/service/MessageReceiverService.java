package ru.devsett.bot.service;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.games.BunkerService;
import ru.devsett.bot.util.ActionDo;
import ru.devsett.bot.util.Role;
import ru.devsett.config.DiscordConfig;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;

import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class MessageReceiverService {

    private final DiscordService discordService;
    private final BunkerService bunkerService;
    private final DiscordConfig discordConfig;
    private final MessageService messageService;
    private final UserService userService;

    @Getter
    private MessageCreateEvent telegramSession;
    @Getter
    private String tokenTelegramSession;

    public MessageReceiverService(DiscordService discordService, BunkerService bunkerService, DiscordConfig discordConfig, MessageService messageService, UserService userService) {
        this.discordService = discordService;
        this.bunkerService = bunkerService;
        this.discordConfig = discordConfig;
        this.messageService = messageService;
        this.userService = userService;
    }

    public void consume(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        if (content.startsWith(discordConfig.getPrefix())
                && content.length() > 2
                && event.getMember().isPresent()
                && !event.getMember().get().isBot()) {
            reflectInvoke(event, content);
        }
    }


    @CommandName(names = {"хелп"})
    public void help(MessageCreateEvent event, String command) {
        var msgHelp =
                "зр - выдать/забрать роль зрителя с перфиксом \"зр.\"\n" +
                        "вд - выдать/забрать роль ведущего с префиксом ! (только для ведущего и опытного)\n" +
                        "Только для ведущего:\n" +
                        "мут - мут всех в канале\n" +
                        "анмут - размут всех в канале\n" +
                        "ордер - выдать номера всем игрокам в комнате кроме роли зр.\n" +
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
                        "персонаж- выдача всем новых персонажей\n" +
                        "персонаж %Начала ника игроков через запятую% - выдать персонажа определенным игрокам\n" +
                        "новый-бункер - выдача всем нового бункера\n" +
                        "катастрофа - выдача всем новой катастрофы\n" +
                        "грязь %ЮзерНейм игрока% - выдаст всю историю сообщений с игроком у бота (последние 2000 символов)\n" +
                        "телеграм %Любой номер% - токен для синхронизации с телеграмом\n" +
                        "рейтинг - показывает ваш рейтинг\n" +
                        "рейтинг %ЮзерНейм% - показывает рейтинг игрока\n" +
                        "\nMADE BY KillSett";

        discordService.sendChatEmbed(event, "Команды", msgHelp, "https://github.com/DevSett/TableTopDiscordBot");
    }

    @CommandName(names = {"рейтинг"})
    public void raite(MessageCreateEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbed(event, "Ваш рейтинг",
                    userService.getOrNewUser(event.getMember().get()).getRating() + "", null);
        } else {
            var user = userService.findByUserName(spl[1]);
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                discordService.sendChatEmbed(event, "Рейтинг " + spl[1], user.getRating() + "" , null);
            }
        }
    }


    @CommandName(names = {"телеграм"})
    public void telegram(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            this.tokenTelegramSession = command.split(" ")[1];
            this.telegramSession = event;
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
        discordService.changeNickName(event, event.getMember().get(), nickName -> !nickName.startsWith("зр.") ? "зр." + nickName : nickName);
    }

    @CommandName(names = {"ведущий", "вд"})
    public void master(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.EXPERT, Role.MASTER)) {
            var action = discordService.addOrRemoveRole(event, Role.MASTER);
            if (action == ActionDo.ADD) {
                discordService.changeNickName(event, event.getMember().get(), nickName -> !nickName.startsWith(discordConfig.getPrefix()) ? "!" + nickName : nickName);
            } else if (action == ActionDo.REMOVE) {
                discordService.changeNickName(event, event.getMember().get(),
                        nickName -> nickName.startsWith(discordConfig.getPrefix()) ? nickName.replace(discordConfig.getPrefix(), "") : nickName);
            }
        }
    }

    @CommandName(names = {"ордер"})
    public void playMafia(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.randomOrderPlayers(event, discordService.getChannelPlayers(event, discordConfig.getPrefix(), "зр."));
        }
    }

    @CommandName(names = {"бункер"})
    public void playBunker(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            discordService.randomOrderPlayers(event, players);
            var bunkerGame = bunkerService.generateGame(players.size());
            for (int index = 0; index < players.size(); index++) {
                discordService.sendPrivateMessage(event, players.get(index), bunkerGame.toString());
                discordService.sendPrivateMessage(event, players.get(index), bunkerGame.getCharacterList().get(index).toString());
            }
        }
    }

    @CommandName(names = {"проф"})
    public void getJobs(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateJobs(players.size());
            getCharacterStats(event, players, commands, ":construction_worker: Новая профессия: ", items);
        }
    }

    @CommandName(names = {"доп"})
    public void getAdditionalInformation(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateAdditionalInformation(players.size());
            getCharacterStats(event, players, commands, ":rainbow: Новая доп. информация: ", items);
        }
    }

    @CommandName(names = {"здоровье", "зд"})
    public void getHealth(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHealths(players.size());
            getCharacterStats(event, players, commands, ":heart: Новое здоровье: ", items);
        }
    }

    @CommandName(names = {"багаж"})
    public void getBaggage(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateBaggage(players.size());
            getCharacterStats(event, players, commands, ":baggage_claim: Новый багаж: ", items);
        }
    }

    @CommandName(names = {"черта"})
    public void getHumanTraits(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHumanTraits(players.size());
            getCharacterStats(event, players, commands, ":face_with_monocle: Новая черта характера: ", items);
        }
    }

    @CommandName(names = {"хобби"})
    public void getHobbes(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHobbes(players.size());
            getCharacterStats(event, players, commands, ":diving_mask: Новое хобби: ", items);
        }
    }

    @CommandName(names = {"персонаж"})
    public void getMaleAndAge(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateMaleAngAgs(players.size());
            getCharacterStats(event, players, commands, ":bust_in_silhouette: Новый персонаж: ", items);
        }
    }

    @CommandName(names = {"новый-бункер"})
    public void getBunker(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            var bunker = bunkerService.generateBunker();
            for (Member player : players) {
                discordService.sendPrivateMessage(event, player, bunker.toStringWithoutDisaster());
            }
        }
    }

    @CommandName(names = {"катастрофа"})
    public void getDisaster(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            var disaster = bunkerService.generateDisaster();
            for (Member player : players) {
                discordService.sendPrivateMessage(event, player, ":t_rex: Новый катаклизм: " + disaster);
            }
        }
    }

    @CommandName(names = {"грязь"})
    public void getMessages(MessageCreateEvent event, String command) {
        var nick = command.split(" ");
        if (discordService.isPresentRole(event, Role.MASTER) && nick.length > 1) {
            var name = nick[1];
            discordService.sendPrivateMessage(event, event.getMember().get(), messageService.getAllMessages(name));
        }
    }

    private void getCharacterStats(MessageCreateEvent event, List<Member> players, String[] commands, String msg, List<String> items) {
        if (commands.length == 1 || commands[1].equals("all")) {
            for (int i = 0; i < items.size(); i++) {
                discordService.sendPrivateMessage(event, players.get(i), msg + items.get(i));
            }
        } else {
            String[] playersSend = commands[1].split(",");
            for (int i = 0; i < playersSend.length; i++) {
                discordService.sendPrivateMessage(event, discordService.getPlayerByStartsWithNick(players, playersSend[i]), msg + items.get(i));
            }
        }
    }

    private void reflectInvoke(MessageCreateEvent event, String content) {
        var command = content.substring(discordConfig.getPrefix().length(), content.contains(" ") ? content.indexOf(" ") : content.length()).trim();
        var findMethod = Arrays.stream(MessageReceiverService.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CommandName.class)
                        && Arrays.asList(method.getDeclaredAnnotation(CommandName.class).names()).contains(command))
                .findFirst();
        findMethod.ifPresent(method -> {
            method.setAccessible(true);
            try {
                method.invoke(this, event, content.substring(discordConfig.getPrefix().length()));
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
            }
        });
    }
}
