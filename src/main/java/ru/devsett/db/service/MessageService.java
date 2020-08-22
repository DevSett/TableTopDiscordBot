package ru.devsett.db.service;

import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;
import ru.devsett.db.dto.MessageEntity;
import ru.devsett.db.repository.MessageRepository;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public MessageEntity sendMessage(Member member, String msg) {
       return newMessage(member, msg, true);
    }

    public MessageEntity receiveMessage(Member member, String msg) {
        return newMessage(member, msg, false);
    }

    public MessageEntity newMessage(Member member, String msg, boolean isSend) {
        var user = userService.getOrNewUser(member);
        var message = new MessageEntity();
        message.setMessage(msg);
        message.setSend(isSend);
        message.setUserEntity(user);
        message.setDateMessage(new Date(System.currentTimeMillis()));
        return messageRepository.save(message);
    }

    public String getAllMessages(String nick) {
        var list = messageRepository.findAllByUserEntity(userService.findByUserName(nick));
        var listMsg = list.stream().map(msg -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(msg.getDateMessage()) + " => "  + msg.getMessage())
                .collect(Collectors.toList());
        return String.join("\n", listMsg);
    }
}
