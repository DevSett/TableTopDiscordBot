package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateEntity;

import java.util.Optional;

public interface WinRateRepository  extends JpaRepository<WinRateEntity, Long> {
    <S extends WinRateEntity> Optional<S> findOneByUserEntity(UserEntity userEntity);
}
