package ru.devsett.bot.service.util;

import org.springframework.stereotype.Service;
import ru.devsett.bot.util.DiscordException;

@Service
public class UtilService {

    public Integer getRate(String strNumber) {
        Integer number = 0;
        try {
            number = Integer.valueOf(strNumber);
        } catch (Exception e) {
            throw new DiscordException("Введите кол-во рейтинга!");
        }
        return number;
    }

    public long getId(String s) {
        try {
            return Long.parseLong(s.substring(3, s.length() - 1));
        } catch (Exception e) {
            throw new DiscordException("Не верно выбран пользователь, нужно указать ссылку(тег) через @");
        }
    }
}
