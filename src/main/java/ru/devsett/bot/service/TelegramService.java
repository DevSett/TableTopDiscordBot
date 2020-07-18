package ru.devsett.bot.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.devsett.bot.util.TelegramCommand;
import ru.devsett.config.TelegramConfig;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class TelegramService extends TelegramLongPollingBot {
    private final TelegramConfig telegramConfig;
    private final ReceiverService receiverService;
    private final DiscordService discordService;

    private Long currentIdSession = 0l;

    public TelegramService(TelegramConfig telegramConfig, ReceiverService receiverService, DiscordService discordService) {
        this.telegramConfig = telegramConfig;
        this.receiverService = receiverService;
        this.discordService = discordService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getCallbackQuery().getMessage();
        }

        if (receiverService.getTokenTelegramSession() != null && receiverService.getTokenTelegramSession().equals(message.getText())) {
            currentIdSession = message.getChatId();
            sendMessage(message.getChatId(), "Команды доступны!", keyboardCommands(TelegramCommand.allValues(), 1, false));
        }

        if (message.getChatId().equals(currentIdSession)) {
            if (message.getText().equals(TelegramCommand.MUTE_ALL.getMsg())) {
                discordService.muteall(receiverService.getTelegramSession());
            }
            if (message.getText().equals(TelegramCommand.UNMUTE_ALL.getMsg())) {
                discordService.unmuteall(receiverService.getTelegramSession());
            }
        }
    }


    @Override
    public String getBotUsername() {
        return telegramConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramConfig.getToken();
    }

    private ReplyKeyboardMarkup keyboardCommands(List<String> listStr, int k, boolean one_time) {

        // k - number of buttons in single line
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow line;
        if (listStr.size() <= k) {
            KeyboardRow keyboardButtons = new KeyboardRow();
            for (String s : listStr) {
                keyboardButtons.add(s);
            }
            keyboard.add(keyboardButtons);
        } else {
            int i = 0;
            while (i < listStr.size()) {
                line = new KeyboardRow();
                for (int j = 0; j < k; j++)
                    if ((i + j) < listStr.size())
                        line.add(listStr.get(j + i));
                keyboard.add(line);
                i = i + k;
            }
        }
        replyKeyboardMarkup.setOneTimeKeyboard(one_time);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);

    }

    public void sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.enableMarkdown(true);
        sendMessage.enableHtml(true);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("sndMsg", e);
        }

    }
}
