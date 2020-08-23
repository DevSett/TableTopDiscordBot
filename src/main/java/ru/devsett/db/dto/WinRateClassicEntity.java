package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_WINRATE_CLASSIC")
public class WinRateClassicEntity {

    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "USER", referencedColumnName = "ID")})
    private UserEntity userEntity;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_WIN_SHERIFF")})
    private Long mafiaWinSheriff = 0L;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_LOSE_SHERIFF")})
    private Long mafiaLoseSheriff = 0L;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_WIN_DON")})
    private Long mafiaWinDon = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_LOSE_DON")})
    private Long mafiaLoseDon = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_WIN_RED")})
    private Long mafiaWinRed = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_LOSE_RED")})
    private Long mafiaLoseRed = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_FIN_BLACK")})
    private Long mafiaWinBlack = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_LOSE_BLACK")})
    private Long mafiaLoseBlack = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_FIND")})
    private Long mafiaFind = 0L;
    ;
    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_MISS")})
    private Long mafiaMiss = 0L;

    @Getter(onMethod_ = {@Basic, @Column(name = "MAFIA_MASTER")})
    private Long mafiaMaster = 0L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WinRateClassicEntity that = (WinRateClassicEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userEntity, that.userEntity) &&
                Objects.equals(mafiaWinSheriff, that.mafiaWinSheriff) &&
                Objects.equals(mafiaLoseSheriff, that.mafiaLoseSheriff) &&
                Objects.equals(mafiaWinDon, that.mafiaWinDon) &&
                Objects.equals(mafiaLoseDon, that.mafiaLoseDon) &&
                Objects.equals(mafiaWinRed, that.mafiaWinRed) &&
                Objects.equals(mafiaLoseRed, that.mafiaLoseRed) &&
                Objects.equals(mafiaWinBlack, that.mafiaWinBlack) &&
                Objects.equals(mafiaLoseBlack, that.mafiaLoseBlack) &&
                Objects.equals(mafiaFind, that.mafiaFind) &&
                Objects.equals(mafiaMiss, that.mafiaMiss);
    }

    @Transient
    public Long totalMafiaGames() {
        return mafiaLoseBlack + mafiaLoseDon + mafiaLoseRed + mafiaLoseSheriff + mafiaWinBlack + mafiaWinDon
                + mafiaWinRed + mafiaWinSheriff;
    }

    @Transient
    public Long totalMafiaWins() {
        return mafiaWinBlack + mafiaWinDon + mafiaWinRed + mafiaWinSheriff;
    }

    @Transient
    public Long totalMafiaLose() {
        return mafiaLoseBlack + mafiaLoseDon + mafiaLoseRed + mafiaLoseSheriff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userEntity, mafiaWinSheriff, mafiaLoseSheriff, mafiaWinDon, mafiaLoseDon, mafiaWinRed, mafiaLoseRed, mafiaWinBlack, mafiaLoseBlack, mafiaFind, mafiaMiss);
    }
}