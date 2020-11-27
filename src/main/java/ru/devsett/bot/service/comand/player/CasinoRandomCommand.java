package ru.devsett.bot.service.comand.player;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.service.comand.MyCommand;
import ru.devsett.bot.service.util.UtilService;
import ru.devsett.db.service.impl.BankService;
import ru.devsett.db.service.impl.UserService;

import java.security.SecureRandom;

@Component
public class CasinoRandomCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public CasinoRandomCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        requiredArgs = 2;
        cooldown = 5;
        requiredChannel = "⡇\uD83D\uDCDFдля-команд";
    }

    @Override
    public String name() {
        return "казино-рояль";
    }

    @Override
    public String help() {
        return "делайте ставку. Шанс выйгрыша 1/10, поставленный коины x5. Будет сыграно числа от 0 до 10, нужно выбрать число. 1-й аргумент число, 2-й аргумент ставка";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var random = new SecureRandom();

        var selectedNumber = utilService.getRate(splitArgs[0]);
        var value = utilService.getRate(splitArgs[1]);
        var user = userService.getOrNewUser(event.getMember());

        if (value > user.getRating() || value < 1) {
            commandEvent.reply("Недостаточно средств!");
            return;
        }

        var casino = random.nextInt(11);

        if (casino == selectedNumber || (user.getId().equals(owner) && selectedNumber == 0)) {
            commandEvent.reply("Выпало число "+ (user.getId().equals(owner) && casino != selectedNumber? 0:casino) + "! Поздравляем вы выйграли " + value*5);
            if (!owner.equals(user.getId())) {
                bankService.addWinBank(Long.valueOf(value));
                bankService.addBalanceBank(value * -1L);
            }
            userService.addRating(event.getGuild(), user, value*4, name, discordService);
        } else {
            commandEvent.reply("Выпало число "+ casino + "! Упс, вы проиграли " + value);
            if (!owner.equals(user.getId())) {
                bankService.addLoseBank(Long.valueOf(value));
                bankService.addBalanceBank(value * 1L);
            }
            userService.addRating(event.getGuild(), user, -1 * value, name, discordService);
        }
    }



}
