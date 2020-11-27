package ru.devsett.bot.service.comand.player;

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
public class AddBankEventCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public AddBankEventCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        this.requiredArgs = 1;
    }

    @Override
    public String name() {
        return "хочуивент";
    }

    @Override
    public String help() {
        return "добавляет коины в банк ивентов. Когда лимит будет достигнут мы проведем ивент! 1 аргумент, кол-во коинов которые вы хотите отправить в банк ивентов.";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var number = utilService.getRate(splitArgs[0]);
        var user = userService.getOrNewUser(event.getMember());

        if (number < 0 || user.getRating() < number) {
            commandEvent.replyError("Не достаточно коинов!");
            return;
        }
        userService.addRating(event.getGuild(), user, number*-1, "банк ивентов", discordService);
        bankService.addBalanceBankIvent(number);
        commandEvent.reply("Вы добавили в баланс банк ивентов: " + number + "; Новый баланс: " + bankService.getBankEvent().getBalance());
    }

}
