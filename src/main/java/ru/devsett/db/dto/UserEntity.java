package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_USER")
public class UserEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID")})
    private Long id;

    @Getter(onMethod_ = {@Basic, @Column(name = "USERNAME")})
    private String userName;
    @Getter(onMethod_ = {@Basic, @Column(name = "NICKNAME")})
    private String nickName;
    @Deprecated
    @Getter(onMethod_ = {@Basic, @Column(name = "COIN")})
    private Long coin = 0l;
    @Getter(onMethod_ = {@Basic, @Column(name = "RATING")})
    private Long rating = 0l;
    @Getter(onMethod_ = {@OneToMany(fetch = FetchType.LAZY, mappedBy = "userEntity")})
    private Collection<MessageEntity> messageEntityByUSER;
    @Getter(onMethod_ = {@Basic, @Column(name = "DATE_BAN")})
    private Date dateBan;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "WHO_BAN", referencedColumnName = "ID")})
    private UserEntity whoBan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(nickName, that.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, nickName);
    }

}
