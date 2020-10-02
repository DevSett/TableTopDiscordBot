package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;

import java.util.List;

@Repository
public interface WhoPlayerHistoryRepository extends JpaRepository<WhoPlayerHistoryEntity, Long> {
    <S extends WhoPlayerHistoryEntity> List<S> findAllByGameHistoryEntity(GameHistoryEntity gameHistoryEntity);
}
