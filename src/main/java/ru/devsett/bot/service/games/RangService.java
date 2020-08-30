package ru.devsett.bot.service.games;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Field;
import ru.devsett.bot.util.Role;
import ru.devsett.db.dto.UserEntity;
import ru.devsett.db.dto.WinRateClassicEntity;
import ru.devsett.db.dto.WinRateEntity;
import ru.devsett.db.service.impl.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class RangService {
    private final UserService userService;
    private final WatchmanService watchmanService;
    private final ChannelService channelService;
    private final DiscordService discordService;
    private final WinRateService winRateService;
    private final WinRateClassicService winRateClassicService;

    public RangService(UserService userService, WatchmanService watchmanService, ChannelService channelService,
                       DiscordService discordService, WinRateService winRateService, WinRateClassicService winRateClassicService) {
        this.userService = userService;
        this.watchmanService = watchmanService;
        this.channelService = channelService;
        this.discordService = discordService;
        this.winRateService = winRateService;
        this.winRateClassicService = winRateClassicService;
    }

    public void join(Member member, VoiceChannel current, VoiceChannel old) {
        var user = userService.getOrNewUser(member);
        var channelEntity = channelService.getOrNewChannel(current.getName(), current.getIdLong(), true);
        watchmanService.join(channelEntity, user, System.currentTimeMillis());
    }

    public void exit(Member member, VoiceChannel current, VoiceChannel old) {
        var user = userService.getOrNewUser(member);
        var channelEntity = channelService.getOrNewChannel(old.getName(), old.getIdLong(), true);
        var watchman = watchmanService.exit(channelEntity, user, System.currentTimeMillis());

        if (watchman != null) {
            var timeSec = (watchman.getExitTime().getTime() - watchman.getJoinTime().getTime()) / 1000;
            var raite = 1d;
            if (member.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_1.getName()))) {
                raite = raite * (timeSec * 0.008);
            } else if (member.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_2.getName()))) {
                raite = raite * (timeSec * 0.012);
            } else if (member.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_3.getName()))) {
                raite = raite * (timeSec * 0.016);
            } else if (member.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_4.getName()))) {
                raite = raite * (timeSec * 0.02);
            } else if (member.getRoles().stream().anyMatch(role -> role.getName().equals(Role.ROLE_5.getName()))) {
                raite = raite * (timeSec * 0.024);
            } else {
                raite = (timeSec * 0.004);
            }

            raite = raite > 116 ? 116 : (int) raite;
            if (raite >= 1) {
                userService.addRating(user, (int) raite, "Watchman", discordService);
            }
        } else {
            throw new DiscordException("WATCHMAN EXIT не найден");
        }


    }

    public List<Field> getWinRateK(UserEntity user) {
        var winRate = winRateClassicService.getOrNewWinRate(user);

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

        list.add(new Field("%", winRateClassicService.getWinRateRed(winRate) + "%"
                + "\n" + winRateClassicService.getWinRateBlack(winRate) + "%"
                + "\n" + winRateClassicService.getWinRateSheriff(winRate) + "%"
                + "\n" + winRateClassicService.getWinRateDon(winRate) + "%"
                + "\n" + winRateClassicService.getWinRateBest(winRate) + "%"
                + "\n"
                + "\n" + winRateClassicService.getTotalRate(winRate) + "%"
                , true));


        return list;
    }

    public List<Field> getTopWinRateK() {
        List<WinRateClassicEntity> list = winRateClassicService.getTopTenForCountGames();
        if (list.isEmpty()) {
            throw new DiscordException("Статистика отсутствует :man_in_manual_wheelchair:");
        } else {
            var fields = new ArrayList<Field>();

            List<String> names = new ArrayList<>();
            List<String> totals = new ArrayList<>();
            List<String> winRate = new ArrayList<>();
            list.stream().forEach(rate -> {
                if (rate.getUserEntity() != null) {
                    names.add("<@!" + rate.getUserEntity().getId() + ">");
                    totals.add(rate.totalMafiaGames() + "");
                    winRate.add((winRateClassicService.getTotalRate(rate) + "%"));
                }
            });

            fields.add(new Field(":detective: Игрок", String.join("\n", names), true));
            fields.add(new Field(":chart_with_upwards_trend: Кол-во игр", String.join("\n", totals), true));
            fields.add(new Field(":military_medal: Общий винрейт", String.join("\n", winRate), true));

            return fields;
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
                + "\n"
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
                if (rate.getUserEntity() != null) {
                    names.add("<@!" + rate.getUserEntity().getId() + ">");
                    totals.add(rate.totalMafiaGames() + "");
                    winRate.add((winRateService.getTotalRate(rate) + "%"));
                }
            });

            fields.add(new Field(":detective: Игрок", String.join("\n", names), true));
            fields.add(new Field(":chart_with_upwards_trend: Кол-во игр", String.join("\n", totals), true));
            fields.add(new Field(":military_medal: Общий винрейт", String.join("\n", winRate), true));

            return fields;
        }
    }
}
