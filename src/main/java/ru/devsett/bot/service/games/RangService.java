package ru.devsett.bot.service.games;

import discord4j.core.object.Embed;
import discord4j.core.object.VoiceState;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Field;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateEntity;
import ru.devsett.db.service.ChannelService;
import ru.devsett.db.service.UserService;
import ru.devsett.db.service.WatchmanService;
import ru.devsett.db.service.WinRateService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RangService {
    private final UserService userService;
    private final WatchmanService watchmanService;
    private final ChannelService channelService;
    private final DiscordService discordService;
    private final WinRateService winRateService;

    public RangService(UserService userService, WatchmanService watchmanService, ChannelService channelService,
                       DiscordService discordService, WinRateService winRateService) {
        this.userService = userService;
        this.watchmanService = watchmanService;
        this.channelService = channelService;
        this.discordService = discordService;
        this.winRateService = winRateService;
    }

    public void join(VoiceState current) {
        var user = userService.getOrNewUser(current.getMember().block());
        var channel = current.getChannel().block();
        var channelEntity = channelService.getOrNewChannel(channel.getName(), channel.getId().asLong(), true);
        watchmanService.join(channelEntity, user, System.currentTimeMillis());
    }

    public void exit(VoiceState current, VoiceState old) {
        var member = current.getMember().block();
        if (member == null) {
            member = old.getMember().block();
        }
        var user = userService.getOrNewUser(member);
        var channel = old.getChannel().block();
        var channelEntity = channelService.getOrNewChannel(channel.getName(), channel.getId().asLong(), true);
        var watchman = watchmanService.exit(channelEntity, user, System.currentTimeMillis());
        var channelNew = current.getChannel().block();
        if (channelNew != null) {
            join(current);
        }
        if (watchman != null) {
            var timeSec = (watchman.getExitTime().getTime() - watchman.getJoinTime().getTime()) / 1000;
            var raite = (timeSec * 0.004);
            raite = raite > 116 ? 116 : (int) raite;
            if (raite >= 1) {
                userService.addRating(user, (int) raite, "Watchman", discordService);
            }
        } else {
            throw new DiscordException("WATCHMAN EXIT не найден");
        }


    }

    public List<Field> getWinRate(UserEntity user) {
        var winRate = winRateService.getOrNewWinRate(user);

        var totalRedWin = winRate.getMafiaWinRed() + winRate.getMafiaWinSheriff();
        var totalRedLose = winRate.getMafiaLoseRed() + winRate.getMafiaLoseSheriff();
        var totalBlackWin = winRate.getMafiaWinBlack() + winRate.getMafiaWinDon();
        var totalBlackLose = winRate.getMafiaLoseBlack() + winRate.getMafiaLoseDon();

        var list = new ArrayList<Field>();
        list.add(new Field("Роли:", ":red_square: Мирный житель:"
                + "\n:black_large_square:  Мафия:"
                + "\n:detective: Комисар:"
                + "\n:woman_vampire: Дон:"
                + "\n:slot_machine: Лучший ход:"
                + "\n:game_die: Провел игр:"
                + "\n:military_medal: Общий винрейт:"
                + "\n:chart_with_upwards_trend: Кол-во игр:", true)
        );
        list.add(new Field("Win/Lose", totalRedWin + "/" + totalRedLose
                + "\n" + totalBlackWin + "/" + totalBlackLose
                + "\n" + winRate.getMafiaWinSheriff() + "/" + winRate.getMafiaLoseSheriff()
                + "\n" + winRate.getMafiaWinDon() + "/" + winRate.getMafiaLoseDon()
                + "\n" + winRate.getMafiaFind() + "/" + winRate.getMafiaMiss()
                + "\n" + winRate.getMafiaMaster()
                + "\n"
                + "\n" + winRate.totalMafiaGames(), true));

        list.add(new Field("%", winRateService.getWinRateRed(winRate) + "%"
                + "\n" + winRateService.getWinRateBlack(winRate) + "%"
                + "\n" + winRateService.getWinRateSheriff(winRate) + "%"
                + "\n" + winRateService.getWinRateDon(winRate) + "%"
                + "\n" + winRateService.getWinRateBest(winRate) + "%"
                + "\n" + winRateService.getTotalRate(winRate) + "%"
                , true));


        return list;
    }

    public List<Field> getTopWinRate() {
        List<WinRateEntity> list = winRateService.getTopTenForCountGames();
        if (list.isEmpty()) {
            throw new DiscordException("Статистика отсутствует :man_in_manual_wheelchair:");
        } else {
            var fields = new ArrayList<Field>();

            List<String> names = new ArrayList<>();
            List<String> totals = new ArrayList<>();
            List<String> winRate = new ArrayList<>();
            list.stream().forEach(rate -> {
                names.add("<@!" + rate.getUserEntity().getId() + ">");
                totals.add(rate.totalMafiaGames() + "");
                winRate.add((winRateService.getTotalRate(rate) + "%"));
            });

            fields.add(new Field(":detective: Игрок", String.join("\n", names), true));
            fields.add(new Field(":chart_with_upwards_trend: Кол-во игр", String.join("\n", totals), true));
            fields.add(new Field(":military_medal: Общий винрейт", String.join("\n", winRate), true));

            return fields;
        }
    }
}
