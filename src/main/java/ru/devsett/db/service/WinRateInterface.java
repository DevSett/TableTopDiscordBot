package ru.devsett.db.service;

import ru.devsett.db.dto.UserEntity;
import java.util.List;

public interface WinRateInterface<T> {
    public T getOrNewWinRate(UserEntity user);

    public List<T> getTopTenForCountGames();

    public Long getTotalRate(T winRate);

    public Long getWinRateRed(T winRate);

    public Long getWinRateBlack(T winRate);

    public Long getWinRateDon(T winRate);

    public Long getWinRateSheriff(T winRate);

    public Long getWinRateBest(T winRate);

    public T addRedWin(UserEntity user);

    public T addRedLose(UserEntity user);

    public T addBlackWin(UserEntity user);

    public T addBlackLose(UserEntity user);

    public T addSheriffWin(UserEntity user);

    public T addSheriffLose(UserEntity user);

    public T addDonWin(UserEntity user);

    public T addDonLose(UserEntity user);

    public T addFind(UserEntity user, Integer count);

    public T addMiss(UserEntity user, Integer count);

    public T addMaster(UserEntity user);
}
