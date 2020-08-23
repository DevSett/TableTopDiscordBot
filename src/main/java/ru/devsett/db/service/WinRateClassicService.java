package ru.devsett.db.service;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateClassicEntity;
import ru.devsett.db.repository.WinRateClassicRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WinRateClassicService {
    private final WinRateClassicRepository winRateClassicRepository;

    public WinRateClassicService(WinRateClassicRepository winRateClassicRepository) {
        this.winRateClassicRepository = winRateClassicRepository;
    }


    public WinRateClassicEntity getOrNewWinRate(UserEntity user) {
        var winRate = winRateClassicRepository.findOneByUserEntity(user);
        if (winRate.isEmpty()) {
            var newWinRate = new WinRateClassicEntity();
            newWinRate.setUserEntity(user);
            return winRateClassicRepository.save(newWinRate);
        }
        return winRate.get();
    }

    public List<WinRateClassicEntity> getTopTenForCountGames() {
        var comparator = Comparator.comparing(WinRateClassicEntity::totalMafiaGames);
        return winRateClassicRepository.findAll()
                .stream().sorted(comparator.reversed())
                .limit(10).collect(Collectors.toList());
    }

    public Long getTotalRate(WinRateClassicEntity winRate) {
       var winRateR = winRate.totalMafiaWins() != 0 && winRate.totalMafiaLose() != 0
                ? ((double) winRate.totalMafiaWins() / (winRate.totalMafiaLose() + winRate.totalMafiaWins()))
                : winRate.totalMafiaWins();

        return Long.valueOf((long) (winRateR * 100 > 100 ? 100 : winRateR * 100));
    }

    public Long getWinRateRed(WinRateClassicEntity winRate) {
        var totalRedWin = winRate.getMafiaWinRed() + winRate.getMafiaWinSheriff();
        var totalRedLose = winRate.getMafiaLoseRed() + winRate.getMafiaLoseSheriff();
        var winRateRed = totalRedLose != 0 && totalRedWin != 0
                ? ((double) totalRedWin / (totalRedLose + totalRedWin))
                : totalRedWin;

        return Long.valueOf((long) (winRateRed * 100 > 100 ? 100 : winRateRed * 100));
    }

    public Long getWinRateBlack(WinRateClassicEntity winRate) {
        var totalBlackWin = winRate.getMafiaWinBlack() + winRate.getMafiaWinDon();
        var totalBlackLose = winRate.getMafiaLoseBlack() + winRate.getMafiaLoseDon();
        var winRateBlack = totalBlackLose != 0 && totalBlackWin != 0
                ? ((double) totalBlackWin / (totalBlackLose + totalBlackWin))
                : totalBlackWin;

        return Long.valueOf((long) (winRateBlack * 100 > 100 ? 100 : winRateBlack * 100));
    }

    public Long getWinRateDon(WinRateClassicEntity winRate) {
        var winRateDon = winRate.getMafiaLoseDon() != 0 && winRate.getMafiaWinDon() != 0
                ? ((double) winRate.getMafiaWinDon() / (winRate.getMafiaWinDon() + winRate.getMafiaLoseDon()))
                : winRate.getMafiaWinDon();

        return Long.valueOf((long) (winRateDon * 100 > 100 ? 100 : winRateDon * 100));
    }

    public Long getWinRateSheriff(WinRateClassicEntity winRate) {
        var winRateSheriff = winRate.getMafiaLoseSheriff() != 0 && winRate.getMafiaWinSheriff() != 0
                ? ((double) winRate.getMafiaWinSheriff() / (winRate.getMafiaLoseSheriff() + winRate.getMafiaWinSheriff()))
                : winRate.getMafiaWinSheriff();

        return Long.valueOf((long) (winRateSheriff * 100 > 100 ? 100 : winRateSheriff * 100));
    }

    public Long getWinRateBest(WinRateClassicEntity winRate) {
        var winRateBest = winRate.getMafiaMiss() != 0 && winRate.getMafiaFind() != 0
                ? ((double) winRate.getMafiaFind() / (winRate.getMafiaMiss() + winRate.getMafiaFind()))
                : winRate.getMafiaFind();

        return Long.valueOf((long) (winRateBest * 100 > 100 ? 100 : winRateBest * 100));
    }

    public WinRateClassicEntity addRedWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinRed(rate.getMafiaWinRed() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addRedLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseRed(rate.getMafiaLoseRed() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addBlackWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinBlack(rate.getMafiaWinBlack() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addBlackLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseBlack(rate.getMafiaLoseBlack() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addSheriffWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinSheriff(rate.getMafiaWinSheriff() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addSheriffLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseSheriff(rate.getMafiaLoseSheriff() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addDonWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinDon(rate.getMafiaWinDon() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addDonLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseDon(rate.getMafiaLoseDon() + 1);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addFind(UserEntity user, Integer count) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaFind(rate.getMafiaFind() + count);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addMiss(UserEntity user, Integer count) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaMiss(rate.getMafiaMiss() + count);
        return winRateClassicRepository.save(rate);
    }

    public WinRateClassicEntity addMaster(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaMaster(Optional.ofNullable(rate.getMafiaMaster()).orElse(0L)+1);
        return winRateClassicRepository.save(rate);
    }
}
