package ru.devsett.db.service.impl;

import org.springframework.stereotype.Service;
import ru.devsett.db.dto.InviteEntity;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.repository.InviteRepository;

@Service
public class InviteService {
    private final InviteRepository inviteRepository;

    public InviteService(InviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    public InviteEntity addInvite(String code, int count, UserEntity userEntity) {
        var invite = new InviteEntity();
        invite.setUserEntity(userEntity);
        invite.setCode(code);
        invite.setCount(count);
        return inviteRepository.save(invite);
    }

    public InviteEntity addInvite(String code, UserEntity userEntity) {
        var invite = new InviteEntity();
        invite.setUserEntity(userEntity);
        invite.setCode(code);
        return inviteRepository.save(invite);
    }

    public InviteEntity updateCount(Integer count, InviteEntity entity) {
        entity.setCount(count);
        return inviteRepository.save(entity);
    }

    public InviteEntity getInvite(String code) {
        return inviteRepository.getOneByCode(code);
    }

    public void deleteInvite(String code) {
        var invite = inviteRepository.getOneByCode(code);
        if (invite != null) {
            inviteRepository.delete(invite);
        }
    }
}
