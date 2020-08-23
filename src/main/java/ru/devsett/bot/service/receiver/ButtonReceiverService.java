package ru.devsett.bot.service.receiver;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

@Service
@Log4j2
public class ButtonReceiverService   extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        System.out.println(event.toString());
    }
}
