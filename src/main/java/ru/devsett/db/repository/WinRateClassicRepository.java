package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateClassicEntity;
import ru.devsett.db.dto.WinRateEntity;

import java.util.Optional;

public interface WinRateClassicRepository extends JpaRepository<WinRateClassicEntity, Long> {
    <S extends WinRateClassicEntity> Optional<S> findOneByUserEntity(UserEntity userEntity);
}
