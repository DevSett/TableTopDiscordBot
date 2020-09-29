package ru.devsett.bot.service.comand;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MyCommand extends Command {

    abstract public void execute(MessageReceivedEvent event, String command);

    @Override
    protected void execute(CommandEvent commandEvent) {
        this.execute(commandEvent.getEvent(), commandEvent.getArgs());
    }
}
