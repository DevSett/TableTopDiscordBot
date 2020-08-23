package ru.devsett.db.dto;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Table(name = "WHO_PLAYER_HISTORY")
public class WhoPlayerHistoryEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "PLAYER", referencedColumnName = "ID")})
    private UserEntity player;
    @Getter(onMethod_ = {@Basic, @Column(name = "IS_RED_PLAYER")})
    private boolean isRedPlayer;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "GAME_HISTORY", referencedColumnName = "ID")})
    private GameHistoryEntity gameHistoryEntity;
}
