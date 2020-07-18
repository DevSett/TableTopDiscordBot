package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_MESSAGE")
public class MessageEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;
    @Getter(onMethod_ = {@ManyToOne(fetch = FetchType.LAZY), @JoinColumn(name = "USER", referencedColumnName = "ID")})
    private UserEntity userEntity;
    @Getter(onMethod_ = {@Basic, @Column(name = "MESSAGE")})
    private String message;
    @Getter(onMethod_ = {@Basic, @Column(name = "IS_SEND")})
    private Character isSend;
    @Getter(onMethod_ = {@Basic, @Column(name = "DATE")})
    private Date dateMessage;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEntity that = (MessageEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userEntity, that.userEntity) &&
                Objects.equals(message, that.message) &&
                Objects.equals(isSend, that.isSend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userEntity, message, isSend);
    }
}
