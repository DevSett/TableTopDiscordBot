package ru.devsett.bot.service.comand;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MyCommand extends Command {

    protected int requiredArgs = 0;
    protected String[] splitArgs;

    public MyCommand() {
        this.guildOnly = false;
        this.name = name();
        this.help = help();
    }

    abstract public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent);
    abstract public String name();
    abstract public String help();

    @Override
    protected void execute(CommandEvent commandEvent) {
        splitArgs = commandEvent.getArgs().split("\\s+");
        if ((commandEvent.getArgs().isEmpty() && requiredArgs > 0) || splitArgs.length < requiredArgs) {
            commandEvent.replyError("Не достаточно аргументов для выполнения команды");
            return;
        }
        this.execute(commandEvent.getEvent(), commandEvent.getArgs(), commandEvent);
    }


}
