package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_INVITE")
public class InviteEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "USER", referencedColumnName = "ID")})
    private UserEntity userEntity;
    @Getter(onMethod_ = {@Basic, @Column(name = "CODE")})
    private String code;
    @Getter(onMethod_ = {@Basic, @Column(name = "COUNT")})
    private int count = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InviteEntity that = (InviteEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userEntity, that.userEntity) &&
                Objects.equals(code, that.code) &&
                Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userEntity, code, count);
    }

    @Override
    public String toString() {
        return "InviteEntity{" +
                "id=" + id +
                ", userEntity=" + userEntity +
                ", code='" + code + '\'' +
                ", count=" + count +
                '}';
    }
}
