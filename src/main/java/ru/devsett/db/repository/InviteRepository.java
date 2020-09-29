package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.InviteEntity;

@Repository
public interface InviteRepository  extends JpaRepository<InviteEntity, Long> {
    InviteEntity getOneByCode(String code);
}
