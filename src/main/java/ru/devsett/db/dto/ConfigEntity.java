package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_CONFIG")
public class ConfigEntity {

    @Getter(onMethod_ = {@Id, @Column(name = "ID"), @GeneratedValue})
    private Long id;

    @Getter(onMethod_ = {@Basic, @Column(name = "ENABLED")})
    private boolean enabled = false;

    @Getter(onMethod_ = {@Basic, @Column(name = "NAME")})
    private String name;

    @Getter(onMethod_ = {@Basic, @Column(name = "VALUE_STRING")})
    private String valueString;

    @Getter(onMethod_ = {@Basic, @Column(name = "TYPE")})
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigEntity that = (ConfigEntity) o;
        return enabled == that.enabled &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(valueString, that.valueString) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enabled, name, valueString, type);
    }
}
