package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateEntity;

import java.util.Optional;

@Repository
public interface WinRateRepository  extends JpaRepository<WinRateEntity, Long> {
    <S extends WinRateEntity> Optional<S> findOneByUserEntity(UserEntity userEntity);
}
