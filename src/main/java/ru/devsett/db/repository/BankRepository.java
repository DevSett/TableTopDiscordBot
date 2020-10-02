package ru.devsett.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.devsett.db.dto.BankEntity;

@Repository
public interface BankRepository extends JpaRepository<BankEntity, Long> {
}
