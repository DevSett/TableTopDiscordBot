package ru.devsett.db.dto;

import lombok.Getter;
import lombok.Setter;
import ru.devsett.bot.util.TypeChannel;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Setter
@Table(name = "DV_CHANNEL")
public class ChannelEntity {
    @Getter(onMethod_ = {@Id, @Column(name = "ID")})
    private Long id;
    @Getter(onMethod_ = {@Basic, @Column(name = "CHANNEL_NAME")})
    private String channelName;
    @Getter(onMethod_ = {@Basic, @Column(name = "IS_VOICE")})
    private boolean isVoice;
    @Getter(onMethod_ = {@OneToMany(fetch = FetchType.LAZY, mappedBy = "channelEntity")})
    private Collection<WatchmanEntity> watchmanEntitiesByChannel;
    @Getter(onMethod_ = {@Basic, @Column(name = "TYPE_CHANNEL")})
    private TypeChannel typeChannel = TypeChannel.DEFAULT_CHANNEL;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelEntity that = (ChannelEntity) o;
        return isVoice == that.isVoice &&
                Objects.equals(id, that.id) &&
                Objects.equals(channelName, that.channelName) &&
                Objects.equals(watchmanEntitiesByChannel, that.watchmanEntitiesByChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, channelName, isVoice, watchmanEntitiesByChannel);
    }
}
