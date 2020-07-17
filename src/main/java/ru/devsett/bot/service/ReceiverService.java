package ru.devsett.bot.service;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.games.BunkerService;
import ru.devsett.bot.util.ActionDo;
import ru.devsett.bot.util.Role;
import ru.devsett.config.DiscordConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Service
public class ReceiverService {

    private final DiscordService discordService;
    private final BunkerService bunkerService;
    private final DiscordConfig discordConfig;

    public ReceiverService(DiscordService discordService, BunkerService bunkerService, DiscordConfig discordConfig) {
        this.discordService = discordService;
        this.bunkerService = bunkerService;
        this.discordConfig = discordConfig;
    }

    public void consume(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        if (content.startsWith(discordConfig.getPrefix()) && content.length() > 2 && event.getMember().isPresent()) {
            var command = content.substring(discordConfig.getPrefix().length(), content.contains(" ") ? content.indexOf(" ") : content.length()).trim();
            var findMethod = Arrays.stream(ReceiverService.class.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(CommandName.class)
                            && Arrays.asList(method.getDeclaredAnnotation(CommandName.class).names()).contains(command))
                    .findFirst();
            findMethod.ifPresent(method -> {
                method.setAccessible(true);
                try {
                    method.invoke(this, event, content.substring(discordConfig.getPrefix().length()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @CommandName(names = {"зр", "зритель", "смотреть", "watch", "watcher"})
    public void watcher(MessageCreateEvent event, String command) {
        discordService.addOrRemoveRole(event, Role.WATCHER);
        discordService.changeNickName(event.getMember().get(), nickName -> !nickName.startsWith("зр.") ? "зр." + nickName : nickName);
    }

    @CommandName(names = {"ведущий", "вд"})
    public void master(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.EXPERT, Role.MASTER)) {
            var action = discordService.addOrRemoveRole(event, Role.MASTER);
            if (action == ActionDo.ADD) {
                discordService.changeNickName(event.getMember().get(), nickName -> !nickName.startsWith(discordConfig.getPrefix()) ?"!" + nickName : nickName);
            } else if (action == ActionDo.REMOVE) {
                discordService.changeNickName(event.getMember().get(),
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
                discordService.sendPrivateMessage(players.get(index), bunkerGame.toString());
                discordService.sendPrivateMessage(players.get(index), bunkerGame.getCharacterList().get(index).toString());
            }
        }
    }

    @CommandName(names = {"проф"})
    public void getJobs(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateJobs(players.size());
            } else {
                items = bunkerService.generateJobs(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":construction_worker: Новая профессия: ", items);
        }
    }

    @CommandName(names = {"доп"})
    public void getAdditionalInformation(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateAdditionalInformation(players.size());
            } else {
                items = bunkerService.generateAdditionalInformation(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":rainbow: Новая доп. информация: ", items);
        }
    }

    @CommandName(names = {"здоровье", "зд"})
    public void getHealth(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateHealths(players.size());
            } else {
                items = bunkerService.generateHealths(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":heart: Новое здоровье: ", items);
        }
    }

    @CommandName(names = {"багаж"})
    public void getBaggage(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateBaggage(players.size());
            } else {
                items = bunkerService.generateBaggage(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":baggage_claim: Новый багаж: ", items);
        }
    }

    @CommandName(names = {"черта"})
    public void getHumanTraits(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateHumanTraits(players.size());
            } else {
                items = bunkerService.generateHumanTraits(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":face_with_monocle: Новая черта характера: ", items);
        }
    }

    @CommandName(names = {"хобби"})
    public void getHobbes(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
                items = bunkerService.generateHobbes(players.size());
            } else {
                items = bunkerService.generateHobbes(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":diving_mask: Новое хобби: ", items);
        }
    }

    @CommandName(names = {"персонаж"})
    public void getMaleAndAge(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            if (commands.length == 1 || commands[1].equals("all")) {
               items = bunkerService.generateMaleAngAgs(players.size());
            } else {
                items = bunkerService.generateHobbes(Integer.parseInt(commands[1]));
            }
            getCharacterStats(players, commands, ":bust_in_silhouette: Новый персонаж: ", items);
        }
    }

    @CommandName(names = {"новый-бункер"})
    public void getBunker(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            var bunker = bunkerService.generateBunker();
            for (Member player : players) {
                discordService.sendPrivateMessage(player, bunker.toStringWithoutDisaster());
            }
        }
    }

    @CommandName(names = {"катастрофа"})
    public void getDisaster(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "зр.");
            var disaster = bunkerService.generateDisaster();
            for (Member player : players) {
                discordService.sendPrivateMessage(player, ":t_rex: Новый катаклизм: " + disaster);
            }
        }
    }

    private void getCharacterStats(List<Member> players, String[] commands, String msg, List<String> items) {
        if (commands.length == 1 || commands[1].equals("all")) {
            for (int i = 0; i < items.size(); i++) {
                discordService.sendPrivateMessage(players.get(i), msg + items.get(i));
            }
        } else {
            String[] playersSend = commands[2].split(",");
            for (int i = 0; i < playersSend.length; i++) {
                discordService.sendPrivateMessage(players.get(Integer.parseInt(playersSend[i]) - 1), msg + items.get(i));
            }
        }
    }


}
