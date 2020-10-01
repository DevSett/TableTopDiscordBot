package ru.devsett.bot.service.games;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.Emoji;
import ru.devsett.bot.util.Player;
import ru.devsett.bot.util.Role;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.service.WinRateInterface;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.UserService;
import ru.devsett.db.service.impl.WinRateClassicService;
import ru.devsett.db.service.impl.WinRateService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MafiaService {

    private final DiscordService discordService;
    private final GameHistoryService gameHistoryService;
    private final WinRateClassicService winRateClassicService;
    private final WinRateService winRateService;
    private final UserService userService;

    public MafiaService(DiscordService discordService, GameHistoryService gameHistoryService, WinRateClassicService winRateClassicService, WinRateService winRateService, UserService userService) {
        this.discordService = discordService;
        this.gameHistoryService = gameHistoryService;
        this.winRateClassicService = winRateClassicService;
        this.winRateService = winRateService;
        this.userService = userService;
    }

    public void createGame(Message msg, MessageReactionAddEvent event) {
        var message = msg.getContentRaw();
        var emj = event.getReactionEmote().getAsReactionCode();
        if (message.equals("Создать классическую мафию") && emj.equals(Emoji.GAME.getName())) {
            var players = discordService.randomMafiaGame(event);
            var game = gameHistoryService.addGame(players, true);
            sendToLS(event, players, game);
            var msgForChat = "Классическая мафия №" + game.getId() + "\nСоздал <@!" + event.getMember().getId() + ">";
            discordService.sendChat(event.getTextChannel(), msgForChat, Emoji.RED, Emoji.BLACK, Emoji.ALERT);
            event.retrieveMessage().queue(msg2 -> {
                msg2.removeReaction(Emoji.GAME.getName(), event.getUser()).queue();
            });
        }
        if (message.equals("Создать городскую мафию") && emj.equals(Emoji.GAME.getName())) {
            var players = discordService.randomMafiaGame(event);
            var game = gameHistoryService.addGame(players, false);
            sendToLS(event, players, game);
            var msgForChat = "Городская мафия №" + game.getId() + "\nСоздал <@!" + event.getMember().getId() + ">";
            discordService.sendChat(event.getTextChannel(), msgForChat, Emoji.RED, Emoji.BLACK, Emoji.ALERT);
            event.retrieveMessage().queue(msg2 -> {
                msg2.removeReaction(Emoji.GAME.getName(), event.getUser()).queue();
            });
        }
        if (message.startsWith("Городская мафия №")) {
            modGameMember(msg, event, message, emj, false);
        }
        if (message.startsWith("Классическая мафия №")) {
            modGameMember(msg, event, message, emj, true);
        }
    }

    @SneakyThrows
    private void modGameMember(Message msg, MessageReactionAddEvent event, String message, String emj, boolean b) {
        var memberId = Long.parseLong(message.substring(message.indexOf("<") + 3, message.indexOf(">")));
        if (memberId != event.getMember().getIdLong() && event.getMember().getRoles().stream().noneMatch(role -> role.getName().equals(Role.DEVELOPER.getName()))) {
            event.retrieveMessage().queue(msg2 -> {
                msg2.removeReaction(event.getReactionEmote().getAsReactionCode(), event.getUser()).queue();
            });
        } else {
            modGame(event, msg, message, emj, b);
        }
    }

    private void modGame(@Nonnull MessageReactionAddEvent event, Message msg, String message, String emj, boolean isClassic) {
        var number = Long.parseLong(message.substring(message.indexOf("№") + 1, message.indexOf("\n")));
        GameHistoryEntity game = null;
        if (emj.equals(Emoji.ALERT.getName())) {
            gameHistoryService.deleteGame(number);
            msg.delete().queue();
            discordService.deleteOrder(event);
        } else if (emj.equals(Emoji.RED.getName())) {
            game = gameHistoryService.win(number, true);
            msg.delete().queue();
            discordService.deleteOrder(event);
        } else if (emj.equals(Emoji.BLACK.getName())) {
            game = gameHistoryService.win(number, false);
            msg.delete().queue();
            discordService.deleteOrder(event);
        }
        try {
            discordService.deleteOrder(event);
        } catch (Exception ex) {
            log.error(ex);
        }
        if (game == null) {
            return;
        }

        if (isClassic) {
            addWinRate(winRateClassicService, game, event.getMember());
        } else {
            addWinRate(winRateService, game, event.getMember());
        }
    }

    private void addWinRate(WinRateInterface winRateInterface, GameHistoryEntity game, Member member) {
        var players = gameHistoryService.getAllWho(game);
        winRateInterface.addMaster(userService.getOrNewUser(member));
        if (game.isWinRed()) {
            if (players.size() > 0) {
                for (WhoPlayerHistoryEntity whoPlayerHistoryEntity : players) {
                    if (whoPlayerHistoryEntity.isRedPlayer()) {
                        winRateInterface.addRedWin(whoPlayerHistoryEntity.getPlayer());
                    } else {
                        winRateInterface.addBlackLose(whoPlayerHistoryEntity.getPlayer());
                    }
                }
            }
            winRateInterface.addDonLose(game.getDonPlayer());
            winRateInterface.addSheriffWin(game.getSheriffPlayer());
        } else {
            if (players.size() > 0) {
                for (WhoPlayerHistoryEntity whoPlayerHistoryEntity : players) {
                    if (whoPlayerHistoryEntity.isRedPlayer()) {
                        winRateInterface.addRedLose(whoPlayerHistoryEntity.getPlayer());
                    } else {
                        winRateInterface.addBlackWin(whoPlayerHistoryEntity.getPlayer());
                    }
                }
            }
            winRateInterface.addDonWin(game.getDonPlayer());
            winRateInterface.addSheriffLose(game.getSheriffPlayer());
        }
    }

    private void sendToLS(@Nonnull MessageReactionAddEvent event, List<Player> players, GameHistoryEntity game) {
        List<String> messagePlayers = new ArrayList<>();
        messagePlayers.add("Игра №" + game.getId());

        for (Player player : players) {
            messagePlayers.add(player.getNumber() + ". " + player.getUserEntity().getUserName() + " - " + player.getMafiaRole().toString());
        }
        discordService.sendPrivateMessageEmbed(event.getMember(), String.join("\n", messagePlayers));
    }
}
