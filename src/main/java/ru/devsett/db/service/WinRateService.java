package ru.devsett.db.service;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateEntity;
import ru.devsett.db.repository.WinRateRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WinRateService {
    private final WinRateRepository winRateRepository;

    public WinRateService(WinRateRepository winRateRepository) {
        this.winRateRepository = winRateRepository;
    }

    public WinRateEntity getOrNewWinRate(UserEntity user) {
        var winRate = winRateRepository.findOneByUserEntity(user);
        if (winRate.isEmpty()) {
            var newWinRate = new WinRateEntity();
            newWinRate.setUserEntity(user);
            return winRateRepository.save(newWinRate);
        }
        return winRate.get();
    }

    public List<WinRateEntity> getTopTenForCountGames() {
        var comparator = Comparator.comparing(WinRateEntity::totalMafiaGames);
        return winRateRepository.findAll()
                .stream().sorted(comparator.reversed())
                .limit(10).collect(Collectors.toList());
    }

    public Long getTotalRate(WinRateEntity winRate) {
       var winRateR = winRate.totalMafiaWins() != 0 && winRate.totalMafiaLose() != 0
                ? ((double) winRate.totalMafiaWins() / (winRate.totalMafiaLose() + winRate.totalMafiaWins()))
                : winRate.totalMafiaWins();

        return Long.valueOf((long) (winRateR * 100 > 100 ? 100 : winRateR * 100));
    }

    public Long getWinRateRed(WinRateEntity winRate) {
        var totalRedWin = winRate.getMafiaWinRed() + winRate.getMafiaWinSheriff();
        var totalRedLose = winRate.getMafiaLoseRed() + winRate.getMafiaLoseSheriff();
        var winRateRed = totalRedLose != 0 && totalRedWin != 0
                ? ((double) totalRedWin / (totalRedLose + totalRedWin))
                : totalRedWin;

        return Long.valueOf((long) (winRateRed * 100 > 100 ? 100 : winRateRed * 100));
    }

    public Long getWinRateBlack(WinRateEntity winRate) {
        var totalBlackWin = winRate.getMafiaWinBlack() + winRate.getMafiaWinDon();
        var totalBlackLose = winRate.getMafiaLoseBlack() + winRate.getMafiaLoseDon();
        var winRateBlack = totalBlackLose != 0 && totalBlackWin != 0
                ? ((double) totalBlackWin / (totalBlackLose + totalBlackWin))
                : totalBlackWin;

        return Long.valueOf((long) (winRateBlack * 100 > 100 ? 100 : winRateBlack * 100));
    }

    public Long getWinRateDon(WinRateEntity winRate) {
        var winRateDon = winRate.getMafiaLoseDon() != 0 && winRate.getMafiaWinDon() != 0
                ? ((double) winRate.getMafiaWinDon() / (winRate.getMafiaWinDon() + winRate.getMafiaLoseDon()))
                : winRate.getMafiaWinDon();

        return Long.valueOf((long) (winRateDon * 100 > 100 ? 100 : winRateDon * 100));
    }

    public Long getWinRateSheriff(WinRateEntity winRate) {
        var winRateSheriff = winRate.getMafiaLoseSheriff() != 0 && winRate.getMafiaWinSheriff() != 0
                ? ((double) winRate.getMafiaWinSheriff() / (winRate.getMafiaLoseSheriff() + winRate.getMafiaWinSheriff()))
                : winRate.getMafiaWinSheriff();

        return Long.valueOf((long) (winRateSheriff * 100 > 100 ? 100 : winRateSheriff * 100));
    }

    public Long getWinRateBest(WinRateEntity winRate) {
        var winRateBest = winRate.getMafiaMiss() != 0 && winRate.getMafiaFind() != 0
                ? ((double) winRate.getMafiaFind() / (winRate.getMafiaMiss() + winRate.getMafiaFind()))
                : winRate.getMafiaFind();

        return Long.valueOf((long) (winRateBest * 100 > 100 ? 100 : winRateBest * 100));
    }

    public WinRateEntity addRedWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinRed(rate.getMafiaWinRed() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addRedLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseRed(rate.getMafiaLoseRed() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addBlackWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinBlack(rate.getMafiaWinBlack() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addBlackLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseBlack(rate.getMafiaLoseBlack() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addSheriffWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinSheriff(rate.getMafiaWinSheriff() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addSheriffLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseSheriff(rate.getMafiaLoseSheriff() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addDonWin(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaWinDon(rate.getMafiaWinDon() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addDonLose(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaLoseDon(rate.getMafiaLoseDon() + 1);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addFind(UserEntity user, Integer count) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaFind(rate.getMafiaFind() + count);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addMiss(UserEntity user, Integer count) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaMiss(rate.getMafiaMiss() + count);
        return winRateRepository.save(rate);
    }

    public WinRateEntity addMaster(UserEntity user) {
        var rate = getOrNewWinRate(user);
        rate.setMafiaMaster(Optional.ofNullable(rate.getMafiaMaster()).orElse(0L)+1);
        return winRateRepository.save(rate);
    }
}
