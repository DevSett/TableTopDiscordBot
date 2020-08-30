package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.db.dto.ChannelEntity;
import ru.devsett.db.repository.ChannelRepository;


@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }


    public ChannelEntity updateType(ChannelEntity ent,TypeChannel type) {
        ent.setTypeChannel(type);
        return channelRepository.save(ent);
    }

    public ChannelEntity getOrNewChannel(String name, Long id, boolean isVoice) {
        var channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            return channel.get();
        }
        var newChannel = new ChannelEntity();
        newChannel.setId(id);
        newChannel.setTypeChannel(TypeChannel.DEFAULT_CHANNEL);
        newChannel.setChannelName(name);
        newChannel.setVoice(isVoice);
        return channelRepository.save(newChannel);
    }

    public void clearTypes() {
        var channels = channelRepository.findAll();
        channels.forEach(ch -> ch.setTypeChannel(TypeChannel.DEFAULT_CHANNEL));
        channelRepository.saveAll(channels);
    }

    public ChannelEntity findByType(TypeChannel typeChannel) {
        return channelRepository.findByTypeChannel(typeChannel).orElse(null);
    }
}
