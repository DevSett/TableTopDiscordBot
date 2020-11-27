package ru.devsett.bot.service.comand.bankir;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.impl.BankService;
import ru.devsett.db.service.impl.UserService;

@Component
public class SetNameEventCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public SetNameEventCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        this.requiredArgs = 1;
        this.requiredRole = Role.BANKIR.getName();
    }

    @Override
    public String name() {
        return "сетнейм";
    }

    @Override
    public String help() {
        return "установит название ивента. Если установить значение \"очистить\" название сотрется.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {

        if (splitArgs[0].equals("очистить")){
            bankService.clearNameEvent();
            commandEvent.reply("Название стерто");

        } else {
            bankService.updateNameEvent(splitArgs[0]);
            commandEvent.reply("Установлено название " + splitArgs[0]);
        }

    }
}
