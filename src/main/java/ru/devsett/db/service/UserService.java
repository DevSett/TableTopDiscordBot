package ru.devsett.db.service;

import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Service;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity newUser(Member member) {
        var user = new UserEntity();
        user.setId(member.getId().asLong());
        user.setUserName(member.getUsername());
        user.setNickName(member.getNickname().orElse(member.getDisplayName()));
        return userRepository.save(user);
    }

    public UserEntity findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity findByNickName(String displayName) {
        return userRepository.findOneByNickName(displayName).orElse(null);
    }

    public UserEntity findByUserName(String nick) {
        return userRepository.findOneByUserName(nick).orElse(null);
    }
}
