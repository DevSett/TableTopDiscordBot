package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.BankEntity;
import ru.devsett.db.repository.BankRepository;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public BankEntity getBank() {
        var banks = bankRepository.findAll();
        if (banks.isEmpty()) {
            var bank = new BankEntity();
            return bankRepository.save(bank);
        }
        return banks.get(0);
    }

    public BankEntity addBalanceBank(Long coin) {
        var bank = getBank();
        bank.setBalance(bank.getBalance()+coin);
        return bankRepository.save(bank);
    }

    public BankEntity addLoseBank(Long coin) {
        var bank = getBank();
        bank.setLoseMoneyCasino(bank.getLoseMoneyCasino()+ coin);
        return bankRepository.save(bank);
    }

    public BankEntity addWinBank(Long coin) {
        var bank = getBank();
        bank.setWinMoneyCasino(bank.getWinMoneyCasino()+ coin);
        return bankRepository.save(bank);
    }
}
