package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.GameHistoryEntity;
import ru.devsett.db.dto.WhoPlayerHistoryEntity;

public interface WhoPlayerHistoryRepository extends JpaRepository<WhoPlayerHistoryEntity, Long> {
}
