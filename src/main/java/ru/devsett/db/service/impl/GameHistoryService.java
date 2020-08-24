package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.bot.util.MafiaRole;
import ru.devsett.bot.util.Player;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.repository.GameHistoryRepository;
import ru.devsett.db.repository.WhoPlayerHistoryRepository;

import java.util.List;

@Service
public class GameHistoryService {
    private final GameHistoryRepository gameHistoryRepository;
    private final WhoPlayerHistoryRepository whoPlayerHistoryRepository;

    public GameHistoryService(GameHistoryRepository gameHistoryRepository, WhoPlayerHistoryRepository whoPlayerHistoryRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
        this.whoPlayerHistoryRepository = whoPlayerHistoryRepository;
    }

    public GameHistoryEntity addGame(List<Player> playerayers, boolean isClassic) {
        var game = new GameHistoryEntity();
        game.setPlayers((long) playerayers.size());
        game.setClassic(isClassic);
        game = gameHistoryRepository.save(game);

        for (Player player : playerayers) {
            if (player.getMafiaRole() == MafiaRole.BLACK || player.getMafiaRole() == MafiaRole.RED) {
                var whoPlayer = new WhoPlayerHistoryEntity();
                whoPlayer.setGameHistoryEntity(game);
                whoPlayer.setPlayer(player.getUserEntity());
                whoPlayer.setRedPlayer(player.getMafiaRole() == MafiaRole.RED);
                whoPlayerHistoryRepository.save(whoPlayer);
            }

            if (player.getMafiaRole() == MafiaRole.DON) {
                game.setDonPlayer(player.getUserEntity());
            }
            if (player.getMafiaRole() == MafiaRole.SHERIFF) {
                game.setSheriffPlayer(player.getUserEntity());
            }
        }
        return gameHistoryRepository.save(game);
    }

    public GameHistoryEntity win(long number, boolean isRedWin) {
        var game = gameHistoryRepository.findById(number);
        if (game.isPresent()) {
            var gameI = game.get();
            gameI.setWinRed(isRedWin);
            gameI.setEndGame(true);
            return gameHistoryRepository.save(gameI);
        }
        return null;
    }

    public void deleteAllStopGames() {
        gameHistoryRepository.findAllByEndGameIsFalse()
                .forEach(game -> whoPlayerHistoryRepository
                        .deleteAll(whoPlayerHistoryRepository.findAllByGameHistoryEntity(game)));
        gameHistoryRepository.deleteAll(gameHistoryRepository.findAllByEndGameIsFalse());
    }
    public List<WhoPlayerHistoryEntity> getAllWho(GameHistoryEntity game) {
       return whoPlayerHistoryRepository.findAllByGameHistoryEntity(game);
    }
    public void deleteGame(long number) {
        whoPlayerHistoryRepository
                .deleteAll(whoPlayerHistoryRepository
                        .findAllByGameHistoryEntity(gameHistoryRepository.getOne(number)));
        gameHistoryRepository.deleteById(number);
    }

}
