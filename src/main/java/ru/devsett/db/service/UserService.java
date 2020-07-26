package ru.devsett.db.service;

import discord4j.core.object.entity.Member;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserEntity getOrNewUser(Member member) {
        if (member == null) {
            throw new DiscordException("Не найден пользователь!");
        }

        var user = findById(member.getId().asLong());
        if (user == null) {
            user = findByUserName(member.getUsername());
        }
        if (user == null) {
            user = new UserEntity();
            user.setId(member.getId().asLong());
        }
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

    public UserEntity addRating(UserEntity user, Integer plus, String from, DiscordService discordService) {
        var raite = user.getRating();

        user.setRating(raite == null ? 0 : user.getRating() + plus);

        var desc = "Для игрока <@!" + user.getId() + "> начислено " + plus + " рейтинга от " + from;
        discordService.toLog("Рейтинг", "Новый рейтинг: " + user.getRating(), desc, Color.SEA_GREEN);

        return userRepository.save(user);
    }

    public List<UserEntity> getUsersForUnBan() {
        var users = userRepository.findAllByDateBanIsNotNull();
        if (users.size() == 0) {
            return users;
        }

        return users.stream().filter(user -> (user.getDateBan() != null && user.getDateBan().before(new Date())))
                .collect(Collectors.toList());
    }

    public void ban(Member member, Member whoBan, int hours) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, hours); // adds one hour

        var user = getOrNewUser(member);
        user.setWhoBan(getOrNewUser(whoBan));
        user.setDateBan(new java.sql.Date(cal.getTime().getTime()));
        userRepository.save(user);
    }

    public void unban(UserEntity user) {
        user.setDateBan(null);
        user.setWhoBan(null);
        userRepository.save(user);
    }

    public String getTop() {
        var users = userRepository.findAllByOrderByRatingDesc();
        AtomicInteger index = new AtomicInteger(1);
        return users.subList(0, 10).stream().map(user -> index.getAndIncrement() + ". " + user.getUserName() + " - " + user.getRating())
                .collect(Collectors.joining("\n"));
    }
}
