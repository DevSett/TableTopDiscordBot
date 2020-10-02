package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateClassicEntity;
import ru.devsett.db.dto.WinRateEntity;

import java.util.Optional;

@Repository
public interface WinRateClassicRepository extends JpaRepository<WinRateClassicEntity, Long> {
    <S extends WinRateClassicEntity> Optional<S> findOneByUserEntity(UserEntity userEntity);
}
