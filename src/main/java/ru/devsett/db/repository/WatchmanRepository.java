package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WatchmanEntity;

import java.util.List;

@Repository
public interface WatchmanRepository extends JpaRepository<WatchmanEntity, Long> {
    List<WatchmanEntity> findAllByUserEntityAndJoinTimeNotNullAndExitTimeIsNull(UserEntity userEntity);
}
