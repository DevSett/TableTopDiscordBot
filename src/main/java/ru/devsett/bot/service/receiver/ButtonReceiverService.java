package ru.devsett.bot.service.receiver;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.Emoji;
import ru.devsett.bot.util.Player;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.service.WinRateInterface;
import ru.devsett.db.service.impl.GameHistoryService;
import ru.devsett.db.service.impl.WinRateClassicService;
import ru.devsett.db.service.impl.WinRateService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ButtonReceiverService extends ListenerAdapter {

    private final DiscordService discordService;
    private final GameHistoryService gameHistoryService;
    private final WinRateService winRateService;
    private final WinRateClassicService winRateClassicService;
    private final MasterReceiverService masterReceiverService;
    public ButtonReceiverService(DiscordService discordService, GameHistoryService gameHistoryService,
                                 WinRateService winRateService, WinRateClassicService winRateClassicService, MasterReceiverService masterReceiverService) {
        this.discordService = discordService;
        this.gameHistoryService = gameHistoryService;
        this.winRateService = winRateService;
        this.winRateClassicService = winRateClassicService;
        this.masterReceiverService = masterReceiverService;
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        event.retrieveMessage().queue(msg -> {
            if (event.getMember().getUser().isBot()) { //TODO
                return;
            }
            var message = msg.getContentDisplay();
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
                modGame(event, msg, message, emj, false);
            }
            if (message.startsWith("Классическая мафия №")) {
                modGame(event,msg,message,emj, true);
            }

        });
    }

    private void modGame(@Nonnull MessageReactionAddEvent event, Message msg, String message, String emj, boolean isClassic) {
        var number = Long.parseLong(message.substring(message.indexOf("№")+1, message.indexOf("\n")));
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
        discordService.deleteOrder(event);

        if (game == null) {
            return;
        }

        if (isClassic) {
            addWinRate(winRateClassicService, game);
        } else {
            addWinRate(winRateService, game);
        }
    }

    private void addWinRate(WinRateInterface winRateInterface, GameHistoryEntity game) {
        var players = gameHistoryService.getAllWho(game);

        if (game.isWinRed()){
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