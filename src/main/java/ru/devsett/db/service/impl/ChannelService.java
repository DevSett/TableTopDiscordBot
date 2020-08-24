package ru.devsett.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.devsett.db.dto.ChannelEntity;
import ru.devsett.db.repository.ChannelRepository;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public ChannelEntity getOrNewChannel(String name, Long id, boolean isVoice) {
        var channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            return channel.get();
        }
        var newChannel = new ChannelEntity();
        newChannel.setId(id);
        newChannel.setChannelName(name);
        newChannel.setVoice(isVoice);
        return channelRepository.save(newChannel);
    }
}
