package ru.devsett.bot.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.devsett.db.dto.UserEntity;

@Data
@AllArgsConstructor
public class Player {
    private UserEntity userEntity;
    private MafiaRole mafiaRole;
}
