package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.bot.util.MafiaRole;
import ru.devsett.bot.util.Player;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;
import ru.devsett.db.repository.GameHistoryRepository;
import ru.devsett.db.repository.WhoPlayerHistoryRepository;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Service
public class GameHistoryService {
    private final GameHistoryRepository gameHistoryRepository;
    private final WhoPlayerHistoryRepository whoPlayerHistoryRepository;
    private final EntityManager entityManager;

    public GameHistoryService(GameHistoryRepository gameHistoryRepository, WhoPlayerHistoryRepository whoPlayerHistoryRepository, EntityManager entityManager) {
        this.gameHistoryRepository = gameHistoryRepository;
        this.whoPlayerHistoryRepository = whoPlayerHistoryRepository;
        this.entityManager = entityManager;
    }

    public List<Long> getLastDonGames(UserEntity userEntity) {
        if (userEntity == null) {
            return Collections.emptyList();
        }

        return entityManager
                .createQuery("select hh.id from GameHistoryEntity hh where hh.donPlayer = :player order by hh.id desc")
                .setMaxResults(5)
                .setParameter("player", userEntity)
                .getResultList();
    }

    public List<Long> getLastSheriffGames(UserEntity userEntity) {
        if (userEntity == null) {
            return Collections.emptyList();
        }

        return entityManager
                .createQuery("select hh.id from GameHistoryEntity hh where hh.sheriffPlayer = :player order by hh.id desc")
                .setMaxResults(5)
                .setParameter("player", userEntity)
                .getResultList();
    }

    public List<Long> getLastRedGames(UserEntity userEntity) {
        if (userEntity == null) {
            return Collections.emptyList();
        }

        return entityManager
                .createQuery("select hh.id from WhoPlayerHistoryEntity hh where hh.player = :player and hh.redPlayer = true order by hh.gameHistoryEntity.id desc")
                .setMaxResults(5)
                .setParameter("player", userEntity)
                .getResultList();
    }

    public List<Long> getLastBlackGames(UserEntity userEntity) {
        if (userEntity == null) {
            return Collections.emptyList();
        }

        return entityManager
                .createQuery("select hh.id from WhoPlayerHistoryEntity hh where hh.player = :player and hh.redPlayer = false order by hh.gameHistoryEntity.id desc")
                .setMaxResults(5)
                .setParameter("player", userEntity)
                .getResultList();
    }
    public GameHistoryEntity getGameById(Integer id) {
        return gameHistoryRepository.findById(Long.valueOf(id)).orElse(null);
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
