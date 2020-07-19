package ru.devsett.db.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.ChannelEntity;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WatchmanEntity;

import java.util.List;
import java.util.Optional;

public interface WatchmanRepository extends JpaRepository<WatchmanEntity, Long> {
    <S extends WatchmanEntity> Optional<S> findOneByChannelEntityAndUserEntityAndJoinTimeNotNullAndExitTimeIsNull(ChannelEntity channelEntity, UserEntity userEntity);
    List<WatchmanEntity> findAllByUserEntityAndJoinTimeNotNullAndExitTimeIsNull(UserEntity userEntity);
}
