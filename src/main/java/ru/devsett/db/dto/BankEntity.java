package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_BANK")
public class BankEntity{

    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;

    @Getter(onMethod_ = {@Basic, @Column(name = "BALANCE")})
    private Long balance = 0l;

    @Getter(onMethod_ = {@Basic, @Column(name = "REQUIRED_BALANCE")})
    private Long requiredBalance = 0l;

    @Getter(onMethod_ = {@Basic, @Column(name = "NAME_EVENT")})
    private String nameEvent;

    @Getter(onMethod_ = {@Basic, @Column(name = "LOSE_MONEY")})
    private Long loseMoneyCasino = 0l;

    @Getter(onMethod_ = {@Basic,@Column(name = "WIN_MONEY")})
    private Long winMoneyCasino = 0l;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankEntity that = (BankEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(requiredBalance, that.requiredBalance) &&
                Objects.equals(loseMoneyCasino, that.loseMoneyCasino) &&
                Objects.equals(winMoneyCasino, that.winMoneyCasino);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, requiredBalance, loseMoneyCasino, winMoneyCasino);
    }
}
