package ru.devsett.bot.service.receiver;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.devsett.bot.MafiaBot;
import ru.devsett.bot.intefaces.CommandName;
import ru.devsett.bot.service.DiscordService;
import ru.devsett.bot.util.DiscordException;
import ru.devsett.bot.util.Role;
import ru.devsett.db.service.MessageService;
import ru.devsett.db.service.UserService;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class MasterReceiverService {
    private final MessageService messageService;
    private final UserService userService;
    private final DiscordService discordService;

    @Getter
    private Member telegramMember;
    @Getter
    private String tokenTelegramSession;

    public MasterReceiverService(MessageService messageService, UserService userService, DiscordService discordService) {
        this.messageService = messageService;
        this.userService = userService;
        this.discordService = discordService;
    }

    @CommandName(names = {"грязь"})
    public void getMessages(MessageCreateEvent event, String command) {
        var nick = command.split(" ");
        if (discordService.isPresentRole(event, Role.MASTER) && nick.length > 1) {
            var name = nick[1];
            discordService.sendPrivateMessage(event, event.getMember().get(), messageService.getAllMessages(name));
        }
    }


    @CommandName(names = {"хелп"})
    public void help(MessageCreateEvent event, String command) {
        var msgHelp =
                "топ - показать топ рейтинга\n" +
                        "зр - выдать/забрать роль зрителя с перфиксом \"Зр.\"\n" +
                        "вд - выдать/забрать роль ведущего без префикса ! (только для ведущего и опытного)\n" +
                        "Только для ведущего:\n" +
                        "мут - мут всех в канале\n" +
                        "анмут - размут всех в канале\n" +
                        "ордер - выдать номера всем игрокам в комнате кроме роли Зр.\n" +
                        "бункер - начать игру бункер (выдача всем в лс их персонажей и состояния бункера с катастрофой)\n" +
                        "проф - выдача всем новых профессий\n" +
                        "проф %Начала ника игроков через запятую% - выдать профессии определенным игрокам\n" +
                        "доп - выдача всем новых доп. информаций\n" +
                        "доп %Начала ника игроков через запятую% - выдать доп. инфу определенным игрокам\n" +
                        "зд - выдача всем новых состояний здоровья \n" +
                        "зд %Начала ника игроков через запятую% - выдать здоровье определенным игрокам\n" +
                        "багаж- выдача всем новых багажей\n" +
                        "багаж %Начала ника игроков через запятую% - выдать багажа определенным игрокам\n" +
                        "телеграм %Любой токен% - токен для синхронизации с телеграмом\n" +
                        "черта- выдача всем новых черт\n" +
                        "черта %Начала ника игроков через запятую% - выдать черту определенным игрокам\n" +
                        "хобби- выдача всем новых  хобби \n" +
                        "хобби %Начала ника игроков через запятую% - выдать определенным игрокам хобби \n" +
                        "фобия- выдача всем новых фобий \n" +
                        "фобия %Начала ника игроков через запятую% - выдать определенным игрокам фобию \n" +
                        "персонаж- выдача всем новых персонажей\n" +
                        "персонаж %Начала ника игроков через запятую% - выдать персонажа определенным игрокам\n" +
                        "новый-бункер - выдача всем нового бункера\n" +
                        "новый-игрок %Начала ника игроков через запятую% - выдать нового игрокового персонажа с новыми характеристиками определенным игрокам\n" +
                        "катастрофа - выдача всем новой катастрофы\n" +
                        "грязь %ЮзерНейм игрока% - выдаст всю историю сообщений с игроком у бота (последние 2000 символов)\n" +
                        "телеграм %Любой номер% - токен для синхронизации с телеграмом\n" +
                        "рейтинг - показывает ваш рейтинг\n" +
                        "рейтинг %ЮзерНейм% - показывает рейтинг игрока\n" +

                        "\nMADE BY KillSett v 0.19";



        discordService.sendChatEmbed(event, "Команды", msgHelp, "https://github.com/DevSett/TableTopDiscordBot");
        if (discordService.isPresentRole(event, Role.MODERATOR)) {
            var  msgHelp2  = "фастбан %Начала никнейма который сидит в вашем войсе% %Причина% %Кол-во часов%\n"
                    + "бан %юзернейм% %Причина% %Кол-во часов%\n"
                    + "анбан %юзернейм%\n"
                    + "адд-рейтинг %юзернейм% %кол-во%\n"
                    + "хайдбан %юзернейм% %кол-в часово%\n"
                    + "хайдфастбан %Начала никнейма который сидит в вашем войсе% %Кол-во часов%";
            discordService.sendChatEmbed(event, "ДЛЯ МОДЕРАТОРОВ", msgHelp2, "https://github.com/DevSett/TableTopDiscordBot");
        }
    }

    @CommandName(names = {"хайдбан"})
    public void hideban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.hideban(event, spl[1], 720);
            }
            if (spl.length == 3) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[2]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.hideban(event, spl[1], number);
            }
        }
    }

    @CommandName(names = {"хайдфастбан"})
    public void hidefastban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.hidefastban(event, spl[1], 720);
            }

            if (spl.length == 3) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[2]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.hidefastban(event, spl[1], number);
            }
        }
    }

    @CommandName(names = {"фастбан"})
    public void fastban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 3) {
                discordService.fastban(event, spl[1], spl[2], 720);
            }
            if (spl.length == 4) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[3]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.fastban(event, spl[1], spl[2], number);
            }
        }
    }

    @CommandName(names = {"бан"})
    public void ban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 3) {
                discordService.ban(event, spl[1], spl[2], 720);
            }
            if (spl.length == 4) {
                Integer number = -1;
                try {
                    number = Integer.valueOf(spl[3]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во часов!");
                }
                discordService.ban(event, spl[1], spl[2], number);
            }
        }
    }

    @CommandName(names = {"анбан"})
    public void unban(MessageCreateEvent event, String command) {
        var spl = command.split(" ");

        if (discordService.isPresentRole(event, Role.MODERATOR) && discordService.isPresentPermission(event, Role.MODERATOR, Permission.BAN_MEMBERS)) {
            if (spl.length == 2) {
                discordService.unban(event, spl[1]);
            }
        }
    }

    @CommandName(names = {"рейтинг"})
    public void raite(MessageCreateEvent event, String command) {
        var spl = command.split(" ");
        if (spl.length == 1) {
            discordService.sendChatEmbed(event, "Ваш рейтинг",
                    userService.getOrNewUser(event.getMember().get()).getRating() + "", null);
        } else {
            var user = userService.findByUserName(spl[1]);
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                discordService.sendChatEmbed(event, "Рейтинг " + spl[1], user.getRating() + "", null);
            }
        }
    }

    @CommandName(names = {"топ"})
    public void top(MessageCreateEvent event, String command) {
        discordService.sendChatEmbed(event, "10 лучших игроков",
                userService.getTop(), null);
    }

    @CommandName(names = {"адд-рейтинг"})
    public void addRaite(MessageCreateEvent event, String command) {
        if (!discordService.isPresentRole(event, Role.MODERATOR)) {
            return;
        }
        var spl = command.split(" ");
        if (spl.length == 2) {
            Integer number = 0;
            try {
                number = Integer.valueOf(spl[1]);
            } catch (Exception e) {
                throw new DiscordException("Введите кол-во рейтинга!");
            }

            var textChannel = MafiaBot.getGuild().getChannels().filter(chan -> chan.getName().equals("log"))
                    .blockFirst();
            var user = userService.getOrNewUser(event.getMember().get());
            if (textChannel instanceof TextChannel) {
                ru.devsett.db.dto.UserEntity finalUser = user;
                int finalRaite = (int) number;
                ((TextChannel) textChannel).createEmbed(spec -> spec.setTitle("Рейтинг")
                        .setDescription("Для игрока " + finalUser.getUserName() + " начислено " + finalRaite + " рейтинга!"  )
                        .setFooter("Рейтинг: " + (userService.findById(finalUser.getId()).getRating()), null))
                        .block();
            }

            discordService.sendChatEmbed(event, "Ваш рейтинг",
                    userService.addRating(user, number).getRating() + "", null);
        } else if (spl.length > 2){
            var user = userService.findByUserName(spl[1]);
            if (user == null) {
                discordService.sendChat(event, "Пользователь не найден!");
            } else {
                Integer number = 0;
                try {
                    number = Integer.valueOf(spl[2]);
                } catch (Exception e) {
                    throw new DiscordException("Введите кол-во рейтинга!");
                }
                var textChannel = MafiaBot.getGuild().getChannels().filter(chan -> chan.getName().equals("log"))
                        .blockFirst();

                if (textChannel instanceof TextChannel) {
                    ru.devsett.db.dto.UserEntity finalUser = user;
                    int finalRaite = (int) number;
                    ((TextChannel) textChannel).createEmbed(spec -> spec.setTitle("Рейтинг")
                            .setDescription("Для игрока " + finalUser.getUserName() + " начислено " + finalRaite + " рейтинга!"  )
                            .setFooter("Рейтинг: " + (userService.findById(finalUser.getId()).getRating()), null))
                            .block();
                }
                discordService.sendChatEmbed(event, "Рейтинг " + spl[1], userService.addRating(user, number).getRating() + "", null);
            }
        }
    }

    @CommandName(names = {"телеграм"})
    public void telegram(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            this.tokenTelegramSession = command.split(" ")[1];
            this.telegramMember = event.getMember().get();
        }
    }

    @CommandName(names = {"мут"})
    public void muteall(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.muteall(event);
        }
    }

    @CommandName(names = {"анмут"})
    public void unmuteall(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.unmuteall(event);
        }
    }

    @CommandName(names = {"зр", "зритель", "смотреть", "watch", "watcher"})
    public void watcher(MessageCreateEvent event, String command) {
        discordService.addOrRemoveRole(event, Role.WATCHER);
        discordService.changeNickName(event, event.getMember().get(), nickName -> !nickName.startsWith("Зр.") ? "Зр." + nickName : nickName);
    }

    @CommandName(names = {"ведущий", "вд"})
    public void master(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.EXPERT, Role.MASTER)) {
            var action = discordService.addOrRemoveRole(event, Role.MASTER);
        }
    }

    @CommandName(names = {"ордер"})
    public void playMafia(MessageCreateEvent event, String command) {
        if (discordService.isPresentRole(event, Role.MASTER)) {
            discordService.randomOrderPlayers(event, discordService.getChannelPlayers(event, "Зр."));
        }
    }

    public void checkOnBan() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            userService.getUsersForUnBan().forEach(user -> {
                try {
                    MafiaBot.getGuild().unban(Snowflake.of(user.getId())).block();
                    try {
                        discordService.addOrRemoveRole(MafiaBot.getGuild(),
                                Optional.ofNullable(MafiaBot.getGuild().getMemberById(Snowflake.of(user.getId())).block()),
                                Role.BAN);
                    } catch (Exception e) {
                        log.error(e);
                    }
                    userService.unban(user);
                } catch (Exception e) {
                    log.error(e);
                }
            });
        }, 0, 15, TimeUnit.SECONDS);
    }
}
