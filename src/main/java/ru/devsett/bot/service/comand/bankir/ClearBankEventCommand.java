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
public class ClearBankEventCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public ClearBankEventCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        this.requiredRole = Role.BANKIR.getName();
    }

    @Override
    public String name() {
        return "списать";
    }

    @Override
    public String help() {
        return "Списать баланс с банка ивентов.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var bank = bankService.getBankEvent();
        bankService.addBalanceBankIvent((int) (bank.getRequiredBalance() * -1));
        commandEvent.reply("Коины списаны. Новый баланс " + bankService.getBankEvent().getBalance());
    }
}
