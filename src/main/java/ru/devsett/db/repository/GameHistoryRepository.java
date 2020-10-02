package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameHistoryRepository extends JpaRepository<GameHistoryEntity, Long> {
    List<GameHistoryEntity> findAllByEndGameIsFalse();
}
