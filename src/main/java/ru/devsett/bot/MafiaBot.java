package ru.devsett.bot;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;
import ru.devsett.bot.service.comand.developer.AddCoinCommand;
import ru.devsett.bot.service.comand.developer.ClearDontStopGameCommand;
import ru.devsett.bot.service.comand.developer.ClearTypeCommand;
import ru.devsett.bot.service.comand.developer.MasterMafiaCommand;
import ru.devsett.bot.service.comand.developer.game.city.*;
import ru.devsett.bot.service.comand.developer.game.city.CancelCityCommand;
import ru.devsett.bot.service.comand.developer.game.classic.*;
import ru.devsett.bot.service.comand.moderator.DirtyCommand;
import ru.devsett.bot.service.comand.moderator.VersionCommand;
import ru.devsett.bot.service.comand.player.*;
import ru.devsett.bot.service.receiver.*;

@Service
@Log4j2
public class MafiaBot {
    private final JDA discordClient;
    private final ButtonReceiverService buttonReceiverService;
    private final JoinReceiverService joinReceiverService;
    private final VoiceReceiverService voiceReceiverService;

    private final LoseBlackCityCommand loseBlackCityCommand;
    private final LoseDonCityCommand loseDonCityCommand;
    private final LoseRedCityCommand loseRedCityCommand;
    private final LoseSheriffCityCommand loseSheriffCityCommand;
    private final WinBlackCityCommand winBlackCityCommand;
    private final WinDonCityCommand winDonCityCommand;
    private final WinRedCityCommand winRedCityCommand;
    private final WinSheriffCityCommand winSheriffCityCommand;

    private final LoseBlackClassicCommand loseBlackClassicCommand;
    private final LoseDonClassicCommand loseDonClassicCommand;
    private final LoseRedClassicCommand loseRedClassicCommand;
    private final LoseSheriffClassicCommand loseSheriffClassicCommand;
    private final WinBlackClassicCommand winBlackClassicCommand;
    private final WinDonClassicCommand winDonClassicCommand;
    private final WinRedClassicCommand winRedClassicCommand;
    private final WinSheriffClassicCommand winSheriffClassicCommand;

    private final AddCoinCommand addCoinCommand;
    private final ClearDontStopGameCommand clearDontStopGameCommand;
    private final ClearTypeCommand clearTypeCommand;
    private final MasterMafiaCommand masterMafiaCommand;

    private final DirtyCommand dirtyCommand;
    private final VersionCommand versionCommand;

    private final BalanceCommand balanceCommand;
    private final CasinoHalfCommand casinoHalfCommand;
    private final CasinoRandomCommand casinoRandomCommand;
    private final GiveCoinCommand giveCoinCommand;
    private final HistoryGameCommand historyGameCommand;
    private final KissCommand kissCommand;
    private final TopClassicCommand topClassicCommand;
    private final TopCoinCommand topCoinCommand;
    private final TopSityCommand topSityCommand;
    private final WinRateCityCommand winRateCityCommand;
    private final WinRateClassicCommand winRateClassicCommand;
    private final JackpotCommand jackpotCommand;
    private final BankCommand bankCommand;
    private final ChangeWinCityCommand changeWinCityCommand;
    private final ChangeWinClassicCommand changeWinClassicCommand;
    private final CancelCityCommand cancelCityCommand;
    private final CancelClassicCommand cancelClassicCommand;
    private final LastGamesCommand lastGamesCommand;
    private final MessageReceiverService messageReceiverService;

    @Getter
    private static JDA jda;

    public MafiaBot(JDA discordClient, ButtonReceiverService buttonReceiverService,
                    JoinReceiverService joinReceiverService, VoiceReceiverService voiceReceiverService,
                    LoseBlackCityCommand loseBlackCityCommand, LoseDonCityCommand loseDonCityCommand,
                    LoseRedCityCommand loseRedCityCommand, LoseSheriffCityCommand loseSheriffCityCommand,
                    WinBlackCityCommand winBlackCityCommand, WinDonCityCommand winDonCityCommand,
                    WinRedCityCommand winRedCityCommand, WinSheriffCityCommand winSheriffCityCommand,
                    LoseBlackClassicCommand loseBlackClassicCommand, LoseDonClassicCommand loseDonClassicCommand,
                    LoseRedClassicCommand loseRedClassicCommand, LoseSheriffClassicCommand loseSheriffClassicCommand,
                    WinBlackClassicCommand winBlackClassicCommand, WinDonClassicCommand winDonClassicCommand,
                    WinRedClassicCommand winRedClassicCommand, WinSheriffClassicCommand winSheriffClassicCommand,
                    AddCoinCommand addCoinCommand, ClearDontStopGameCommand clearDontStopGameCommand,
                    ClearTypeCommand clearTypeCommand, MasterMafiaCommand masterMafiaCommand, DirtyCommand dirtyCommand,
                    VersionCommand versionCommand, BalanceCommand balanceCommand, CasinoHalfCommand casinoHalfCommand,
                    CasinoRandomCommand casinoRandomCommand, GiveCoinCommand giveCoinCommand, HistoryGameCommand historyGameCommand,
                    KissCommand kissCommand, TopClassicCommand topClassicCommand, TopCoinCommand topCoinCommand, TopSityCommand topSityCommand,
                    WinRateCityCommand winRateCityCommand, WinRateClassicCommand winRateClassicCommand, JackpotCommand jackpotCommand, BankCommand bankCommand, ChangeWinCityCommand changeWinCityCommand, ChangeWinClassicCommand changeWinClassicCommand, CancelCityCommand cancelCityCommand, CancelClassicCommand cancelClassicCommand, LastGamesCommand lastGamesCommand, MessageReceiverService messageReceiverService) {
        this.discordClient = discordClient;
        this.buttonReceiverService = buttonReceiverService;
        this.joinReceiverService = joinReceiverService;
        this.voiceReceiverService = voiceReceiverService;
        this.loseBlackCityCommand = loseBlackCityCommand;
        this.loseDonCityCommand = loseDonCityCommand;
        this.loseRedCityCommand = loseRedCityCommand;
        this.loseSheriffCityCommand = loseSheriffCityCommand;
        this.winBlackCityCommand = winBlackCityCommand;
        this.winDonCityCommand = winDonCityCommand;
        this.winRedCityCommand = winRedCityCommand;
        this.winSheriffCityCommand = winSheriffCityCommand;
        this.loseBlackClassicCommand = loseBlackClassicCommand;
        this.loseDonClassicCommand = loseDonClassicCommand;
        this.loseRedClassicCommand = loseRedClassicCommand;
        this.loseSheriffClassicCommand = loseSheriffClassicCommand;
        this.winBlackClassicCommand = winBlackClassicCommand;
        this.winDonClassicCommand = winDonClassicCommand;
        this.winRedClassicCommand = winRedClassicCommand;
        this.winSheriffClassicCommand = winSheriffClassicCommand;
        this.addCoinCommand = addCoinCommand;
        this.clearDontStopGameCommand = clearDontStopGameCommand;
        this.clearTypeCommand = clearTypeCommand;
        this.masterMafiaCommand = masterMafiaCommand;
        this.dirtyCommand = dirtyCommand;
        this.versionCommand = versionCommand;
        this.balanceCommand = balanceCommand;
        this.casinoHalfCommand = casinoHalfCommand;
        this.casinoRandomCommand = casinoRandomCommand;
        this.giveCoinCommand = giveCoinCommand;
        this.historyGameCommand = historyGameCommand;
        this.kissCommand = kissCommand;
        this.topClassicCommand = topClassicCommand;
        this.topCoinCommand = topCoinCommand;
        this.topSityCommand = topSityCommand;
        this.winRateCityCommand = winRateCityCommand;
        this.winRateClassicCommand = winRateClassicCommand;
        this.jackpotCommand = jackpotCommand;
        this.bankCommand = bankCommand;
        this.changeWinCityCommand = changeWinCityCommand;
        this.changeWinClassicCommand = changeWinClassicCommand;
        this.cancelCityCommand = cancelCityCommand;
        this.cancelClassicCommand = cancelClassicCommand;
        this.lastGamesCommand = lastGamesCommand;
        this.messageReceiverService = messageReceiverService;
    }

    @SneakyThrows
    public void init() {
        this.jda = discordClient;

        CommandClientBuilder commandClientBuilder = new CommandClientBuilder();
        commandClientBuilder.setPrefix("!");
        commandClientBuilder.setOwnerId("183893341220503552");
        commandClientBuilder.setHelpWord("хелп");
        commandClientBuilder.addCommands(this.loseBlackCityCommand,
                this.loseDonCityCommand,
                this.loseRedCityCommand,
                this.loseSheriffCityCommand,
                this.winBlackCityCommand,
                this.winDonCityCommand,
                this.winRedCityCommand,
                this.winSheriffCityCommand,
                this.loseBlackClassicCommand,
                this.loseDonClassicCommand,
                this.loseRedClassicCommand,
                this.loseSheriffClassicCommand,
                this.winBlackClassicCommand,
                this.winDonClassicCommand,
                this.winRedClassicCommand,
                this.winSheriffClassicCommand,
                this.addCoinCommand,
                this.clearDontStopGameCommand,
                this.clearTypeCommand,
                this.masterMafiaCommand,
                this.dirtyCommand,
                this.versionCommand,
                this.balanceCommand,
                this.casinoHalfCommand,
                this.casinoRandomCommand,
                this.historyGameCommand,
                this.topClassicCommand,
                this.topCoinCommand,
                this.topSityCommand,
                this.winRateCityCommand,
                this.winRateClassicCommand,
                this.kissCommand,
                this.giveCoinCommand,
                this.jackpotCommand,
                this.bankCommand,
                this.changeWinCityCommand,
                this.changeWinClassicCommand,
                this.cancelCityCommand,
                this.cancelClassicCommand,
                this.lastGamesCommand
                );

        discordClient.addEventListener(messageReceiverService, voiceReceiverService, buttonReceiverService, joinReceiverService,
                commandClientBuilder.build());

        discordClient.awaitReady();
    }
}

