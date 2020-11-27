package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.BankService;
import ru.devsett.db.service.impl.UserService;

@Component
public class BankEventCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public BankEventCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;
    }

    @Override
    public String name() {
        return "банк";
    }

    @Override
    public String help() {
        return "баланс банка ивентов";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var bank = bankService.getBankEvent();
        var name = bank.getNameEvent();
        commandEvent.reply((name!= null? "Сбор коинов на ивент: " + name + "; ": " ") + "Коинов в банке ивентов: "
                + bank.getBalance() +"; Необходимо коинов на ивент: " + bank.getRequiredBalance());
    }



}
