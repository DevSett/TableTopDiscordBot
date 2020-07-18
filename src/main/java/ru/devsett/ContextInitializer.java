package ru.devsett;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.devsett.bot.MafiaBot;


@SpringBootApplication
@EnableConfigurationProperties
public class ContextInitializer implements CommandLineRunner {

    private final MafiaBot mafiaBot;

    public ContextInitializer(MafiaBot mafiaBot) {
        this.mafiaBot = mafiaBot;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(ContextInitializer.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        mafiaBot.init();
    }
}
