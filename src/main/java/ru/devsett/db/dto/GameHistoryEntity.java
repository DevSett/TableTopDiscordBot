package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_GAME_HISTORY")
public class GameHistoryEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@Basic, @Column(name = "IS_CLASSIC")})
    private boolean isClassic;
    @Getter(onMethod_ = {@Basic, @Column(name = "IS_WIN_RED")})
    private boolean isWinRed;
    @Getter(onMethod_ = {@OneToMany(fetch = FetchType.LAZY, mappedBy = "gameHistoryEntity")})
    private Collection<WhoPlayerHistoryEntity> whoPlayerHistoryEntityByGameHistory;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "DON_PLAYER", referencedColumnName = "ID")})
    private UserEntity donPlayer;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "SHERIFF_PLAYER", referencedColumnName = "ID")})
    private UserEntity sheriffPlayer;

    @Getter(onMethod_ = {@Basic, @Column(name = "PLAYERS")})
    private Long players = 0L;

}
