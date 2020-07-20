package ru.devsett.bot.service.receiver;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.games.BunkerService;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;
import ru.devsett.game.bunker.Character;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BunkerReceiverService {

    private final BunkerService bunkerService;
    private final DiscordService discordService;

    public BunkerReceiverService( BunkerService bunkerService, DiscordService discordService) {
        this.bunkerService = bunkerService;
        this.discordService = discordService;
    }

    @CommandName(names = {"новый-игрок"})
    public void newPlayer(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            String[] commands = command.split(" ");
            var players = discordService.getChannelPlayers(event, "Зр.");
            var bunkerGame = bunkerService.generateGame(players.size());
            getCharacterStats(event, players, commands, "Новый игрок: ", bunkerGame.getCharacterList().stream().map(Character::toString).collect(Collectors.toList()));

        }
    }

    @CommandName(names = {"бункер"})
    public void playBunker(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
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
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateJobs(players.size());
            getCharacterStats(event, players, commands, ":construction_worker: Новая профессия: ", items);
        }
    }

    @CommandName(names = {"доп"})
    public void getAdditionalInformation(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateAdditionalInformation(players.size());
            getCharacterStats(event, players, commands, ":rainbow: Новая доп. информация: ", items);
        }
    }

    @CommandName(names = {"фобия"})
    public void getPhobia(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generatePhobia(players.size());
            getCharacterStats(event, players, commands, ":rainbow: Новая фобия: ", items);
        }
    }

    @CommandName(names = {"здоровье", "зд"})
    public void getHealth(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHealths(players.size());
            getCharacterStats(event, players, commands, ":heart: Новое здоровье: ", items);
        }
    }

    @CommandName(names = {"багаж"})
    public void getBaggage(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateBaggage(players.size());
            getCharacterStats(event, players, commands, ":baggage_claim: Новый багаж: ", items);
        }
    }

    @CommandName(names = {"черта"})
    public void getHumanTraits(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHumanTraits(players.size());
            getCharacterStats(event, players, commands, ":face_with_monocle: Новая черта характера: ", items);
        }
    }

    @CommandName(names = {"хобби"})
    public void getHobbes(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateHobbes(players.size());
            getCharacterStats(event, players, commands, ":diving_mask: Новое хобби: ", items);
        }
    }

    @CommandName(names = {"персонаж"})
    public void getMaleAndAge(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            String[] commands = command.split(" ");
            List<String> items = null;
            items = bunkerService.generateMaleAngAgs(players.size());
            getCharacterStats(event, players, commands, ":bust_in_silhouette: Новый персонаж: ", items);
        }
    }

    @CommandName(names = {"новый-бункер"})
    public void getBunker(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            var bunker = bunkerService.generateBunker();
            for (Member player : players) {
                discordService.sendPrivateMessage(event, player, bunker.toStringWithoutDisaster());
            }
        }
    }

    @CommandName(names = {"катастрофа"})
    public void getDisaster(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            var players = discordService.getChannelPlayers(event, "Зр.");
            var disaster = bunkerService.generateDisaster();
            for (Member player : players) {
                discordService.sendPrivateMessage(event, player, ":t_rex: Новый катаклизм: " + disaster);
            }
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

}
