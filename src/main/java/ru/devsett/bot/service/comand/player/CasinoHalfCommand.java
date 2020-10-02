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
import java.util.ArrayList;
import java.util.List;

@Component
public class CasinoHalfCommand extends MyCommand {

    private final DiscordService discordService;
    private final UserService userService;
    private final UtilService utilService;
    private final BankService bankService;

    public CasinoHalfCommand(DiscordService discordService, UserService userService, UtilService utilService, BankService bankService) {
        this.userService = userService;
        this.utilService = utilService;
        this.discordService = discordService;
        this.bankService = bankService;

        requiredArgs = 1;
        cooldown = 10;
        requiredChannel = "⡇\uD83D\uDCDFдля-команд";
    }

    @Override
    public String name() {
        return "казино-фифти";
    }

    @Override
    public String help() {
        return "делайте ставку. Шанс выйгрыша 50/50, поставленные койны удвоятся. 1-й аргумент кол-во коинов";
    }

    @Override
    public void execute(MessageReceivedEvent event, String command, CommandEvent commandEvent) {
        var random = new SecureRandom();

        var value = utilService.getRate(splitArgs[0]);
        var user = userService.getOrNewUser(event.getMember());

        if (value > user.getRating() || value < 1) {
            commandEvent.reply("Недостаточно средств!");
            return;
        }

        List<Boolean> games = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            games.add(true);
        }
        for (int i = 0; i < 11; i++) {
            games.add(false);
        }

        var casino = games.get(random.nextInt(games.size()));
        if (casino || (owner.equals(user.getId()) && (games.get(random.nextInt(games.size()))))) {
            commandEvent.reply("Поздравляем вы выиграли " + value);
            if (!owner.equals(user.getId())) {
                bankService.addWinBank(Long.valueOf(value));
                bankService.addBalanceBank(value * -1L);
            }
            userService.addRating(event.getGuild(), user, value, name, discordService);
        } else {
            commandEvent.reply("Упс, вы проиграли " + value);
            if (!owner.equals(user.getId())) {
                bankService.addLoseBank(Long.valueOf(value));
                bankService.addBalanceBank(value * 1L);
            }
            userService.addRating(event.getGuild(), user, -1 * value, name, discordService);
        }
    }
}
