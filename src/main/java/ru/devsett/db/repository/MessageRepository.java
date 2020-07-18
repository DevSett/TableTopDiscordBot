package ru.devsett.db.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.MessageEntity;
import ru.devsett.db.dto.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllByUserEntity(UserEntity userEntity);
}
