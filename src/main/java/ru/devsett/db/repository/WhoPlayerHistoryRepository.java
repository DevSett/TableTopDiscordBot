package ru.devsett.db.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;

import java.util.List;

public interface WhoPlayerHistoryRepository extends JpaRepository<WhoPlayerHistoryEntity, Long> {
    <S extends WhoPlayerHistoryEntity> List<S> findAllByGameHistoryEntity(GameHistoryEntity gameHistoryEntity);
}
