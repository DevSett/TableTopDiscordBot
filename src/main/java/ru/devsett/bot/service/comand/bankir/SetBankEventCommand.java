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
public class SetBankEventCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public SetBankEventCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        this.requiredArgs = 1;
        this.requiredRole = Role.BANKIR.getName();
    }

    @Override
    public String name() {
        return "сетбанк";
    }

    @Override
    public String help() {
        return "установит кол-во необходимых коинов на ивентов";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var number = utilService.getRate(splitArgs[0]);
        if (number < 0) {
            commandEvent.replyError("Вы идиот?");
            return;
        }

        bankService.updateRequiredEvent(number);
        commandEvent.reply("Установлен лимит в " + number);
    }
}
