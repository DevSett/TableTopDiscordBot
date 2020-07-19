package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_WATCHMAN")
public class WatchmanEntity {

    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "USER", referencedColumnName = "ID")})
    private UserEntity userEntity;
    @Getter(onMethod_ = {@Basic, @Column(name = "JOIN_TIME")})
    private Date joinTime;
    @Getter(onMethod_ = {@Basic, @Column(name = "EXIT_TIME")})
    private Date exitTime;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "CHANNEL", referencedColumnName = "ID")})
    private ChannelEntity channelEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchmanEntity that = (WatchmanEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userEntity, that.userEntity) &&
                Objects.equals(joinTime, that.joinTime) &&
                Objects.equals(exitTime, that.exitTime) &&
                Objects.equals(channelEntity, that.channelEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userEntity, joinTime, exitTime, channelEntity);
    }
}
