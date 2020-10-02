package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    <S extends UserEntity> Optional<S> findOneByNickName(String displayName);
    <S extends UserEntity> Optional<S> findOneByUserName(String displayName);
    <S extends UserEntity> List<UserEntity> findAllByDateBanIsNotNull();
    <S extends UserEntity> List<UserEntity> findAllByOrderByRatingDesc();

}
