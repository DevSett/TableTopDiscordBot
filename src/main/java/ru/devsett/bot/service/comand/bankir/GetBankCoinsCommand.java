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

import java.awt.*;

@Component
public class GetBankCoinsCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public GetBankCoinsCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        this.requiredArgs = 2;
        this.requiredRole = Role.BANKIR.getName();
    }

    @Override
    public String name() {
        return "взять";
    }

    @Override
    public String help() {
        return "вы берете коины из банка. 1 аргумент - кол-во, 2 - причина";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var number = utilService.getRate(splitArgs[0]);
        var bank = bankService.getBank();
        if (number < 0 || bank.getBalance() < number) {
            commandEvent.replyError("Вы идиот? Не достаточно коинов.");
            return;
        }

        discordService.toLogTextChannel("Взяты коины из банка в кол-ве " + number,
                "По причине: " + splitArgs[1], event, Color.YELLOW.getRGB());

        bankService.addBalanceBank(number * -1L);
        userService.addRating(event.getGuild(), userService.getOrNewUser(event.getMember()), number, "банкир",
                discordService);

        commandEvent.reply("Вы взяли из банка " + number);
    }
}
