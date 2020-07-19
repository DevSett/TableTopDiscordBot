package ru.devsett.db.service;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.ChannelEntity;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WatchmanEntity;
import ru.devsett.db.repository.WatchmanRepository;

import java.sql.Date;

@Service
public class WatchmanService {
    private final WatchmanRepository watchmanRepository;

    public WatchmanService(WatchmanRepository watchmanRepository) {
        this.watchmanRepository = watchmanRepository;
    }

    public WatchmanEntity join(ChannelEntity channelEntity,UserEntity user, long sysmilsJoin) {
        destroyOtherWatchman(user);
        var watchman = new WatchmanEntity();
        watchman.setChannelEntity(channelEntity);
        watchman.setUserEntity(user);
        watchman.setJoinTime(new Date(sysmilsJoin));
        return watchmanRepository.save(watchman);
    }

    public WatchmanEntity exit(ChannelEntity channelEntity, UserEntity user, long sysmilsExit) {
        var watchman = watchmanRepository.findOneByChannelEntityAndUserEntityAndJoinTimeNotNullAndExitTimeIsNull(channelEntity,user);
        if (watchman.isEmpty()) {
            return null;
        }
        var watchmanGet = watchman.get();
        watchmanGet.setExitTime(new Date(sysmilsExit));

        return watchmanRepository.save(watchmanGet);
    }

    public void destroyOtherWatchman(UserEntity userEntity) {
        var watchmans = watchmanRepository.findAllByUserEntityAndJoinTimeNotNullAndExitTimeIsNull(userEntity);
        if (watchmans.size() > 0) {
            watchmans.forEach(watch -> watch.setExitTime(watch.getJoinTime()));
        }
        watchmanRepository.saveAll(watchmans);
    }
}
