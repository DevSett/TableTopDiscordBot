package ru.devsett.db.service;

import org.springframework.stereotype.Service;
import ru.devsett.bot.util.DiscordException;
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
        var watchman = watchmanRepository.findAllByUserEntityAndJoinTimeNotNullAndExitTimeIsNull(user);
        if (watchman.isEmpty()) {
            throw  new DiscordException("WATCHMAN EXIT не найден");
        }
        var watchmanGet = watchman.stream().filter(watchmanEntity -> watchmanEntity.getChannelEntity() != null &&
                watchmanEntity.getId() == channelEntity.getId())
                .findFirst();
        var findWatch = watchmanGet.orElse(watchman.get(0));
        findWatch.setExitTime(new Date(sysmilsExit));
        var rtrn = watchmanRepository.save(findWatch);

        destroyOtherWatchman(user);

        return rtrn;
    }

    public void destroyOtherWatchman(UserEntity userEntity) {
        var watchmans = watchmanRepository.findAllByUserEntityAndJoinTimeNotNullAndExitTimeIsNull(userEntity);
        if (watchmans.size() > 0) {
            watchmans.forEach(watch -> watch.setExitTime(watch.getJoinTime()));
        }
        watchmanRepository.saveAll(watchmans);
    }
}
