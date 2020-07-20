package ru.devsett.bot.service.receiver;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.http.client.ClientException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.config.DiscordConfig;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
@Log4j2
public class MessageReceiverService {

    private final DiscordConfig discordConfig;
    private final DiscordService discordService;
    private final MasterReceiverService masterReceiverService;
    private final BunkerReceiverService bunkerReceiverService;


    public MessageReceiverService(DiscordService discordService, DiscordConfig discordConfig, MasterReceiverService masterReceiverService, BunkerReceiverService bunkerReceiverService) {
        this.discordService = discordService;
        this.discordConfig = discordConfig;
        this.masterReceiverService = masterReceiverService;
        this.bunkerReceiverService = bunkerReceiverService;
    }

    public void consume(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();
        if (content.startsWith(discordConfig.getPrefix())
                && content.length() > 2
                && event.getMember().isPresent()
                && !event.getMember().get().isBot()) {
            reflectInvoke(event, content);
        }
    }


    private void reflectInvoke(MessageCreateEvent event, String content) {
        var command = content.substring(discordConfig.getPrefix().length(), content.contains(" ") ? content.indexOf(" ") : content.length()).trim();
        var findMethod = getMethodStream(MasterReceiverService.class, command).findFirst();
        var secondMethod = getMethodStream(BunkerReceiverService.class, command).findFirst();

        findMethod.ifPresent(method -> {
            invoke(event, masterReceiverService, content, method);
        });
        secondMethod.ifPresent(method -> {
            invoke(event, bunkerReceiverService, content, method);
        });

    }

    private void invoke(MessageCreateEvent event, Object object, String content, Method method) {
        method.setAccessible(true);
        try {
            method.invoke(object, event, content.substring(discordConfig.getPrefix().length()));
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof ClientException) {
                ClientException clientException = (ClientException) e.getTargetException();
                if (clientException.getStatus() == HttpResponseStatus.FORBIDDEN) {
                    discordService.sendChat(event, "Недостаточно прав!");
                }
            }
            if (e.getTargetException() instanceof DiscordException) {
                DiscordException discordException = (DiscordException) e.getTargetException();
                discordService.sendChat(event, discordException.getMessage());
            }
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        }
    }

    private Stream<Method> getMethodStream(Class cls, String command) {
        return Arrays.stream(cls.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CommandName.class)
                        && Arrays.asList(method.getDeclaredAnnotation(CommandName.class).names()).contains(command));
    }

}
