package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.bot.util.TypeChannel;
import ru.devsett.db.dto.ChannelEntity;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelEntity> findByTypeChannel(TypeChannel typeChannel);
}
