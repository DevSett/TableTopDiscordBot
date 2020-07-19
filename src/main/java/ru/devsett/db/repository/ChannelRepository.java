package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.ChannelEntity;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
}
