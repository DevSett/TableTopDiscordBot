package ru.devsett.bot.service.games;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.devsett.game.bunker.Bunker;
import ru.devsett.game.bunker.Character;
import ru.devsett.game.bunker.json.BunkerGameModel;
import ru.devsett.game.bunker.json.CharacterModel;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BunkerService {

    final ResourceLoader resourceLoader;

    public BunkerService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @SneakyThrows
    public Bunker generateGame(int size) {
        var bunkerGameModel = readBunker();
        return generateBunker(bunkerGameModel, size);
    }

    public List<String> generateJobs(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getJobs(), size)
                .stream().map(job -> job + " (" + randomItem(bunkerGameModel.getCharacter().getJobStages()) + ")")
                .collect(Collectors.toList());
    }

    public List<String> generateAdditionalInformation(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getAdditionalInformation(), size);
    }

    public List<String> generateHealths(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getHealths(), size)
                .stream().map(health -> health + " (Тяжесть: " + random(10, 101) + "%)")
                .collect(Collectors.toList());
    }

    public List<String> generateBaggage(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getBaggage(), size);
    }

    public List<String> generateHumanTraits(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getHumanTraits(), size);
    }

    public List<String> generateHobbes(int size) {
        var bunkerGameModel = readBunker();
        return randomList(bunkerGameModel.getCharacter().getHobbes(), size)
                .stream().map(hobby -> hobby + " (" + randomItem(bunkerGameModel.getCharacter().getHobbyStages()) + ")")
                .collect(Collectors.toList());
    }

    public List<String> generateMaleAngAgs(int size) {
        var bunkerGameModel = readBunker();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(generateMaleAngAgs());
        }
        return list;

    }

    public String generateMaleAngAgs() {
        var bunkerGameModel = readBunker();
        return randomItem(bunkerGameModel.getCharacter().getMales())
                + ", Возраст: "
                + generateNumberBySplitNumberMany(bunkerGameModel.getCharacter().getAges())
                + (new SecureRandom().nextBoolean() ? " (Чайлдфри)" : " (Не чайлдфри)");
    }



    public Bunker generateBunker() {
        var bunkerGame = new Bunker();
        var bunkerGameModel = readBunker();
        var bunker = bunkerGameModel.getBunker();

        bunkerGame.setArea(randomItem(bunker.getAreas()));
        bunkerGame.setAdditionalItems(randomList(bunker.getAdditionalItems(), random(4)));
        bunkerGame.setDescription(randomItem(bunker.getDescriptions()));
        bunkerGame.setRooms(randomList(bunker.getRooms(), random(4)));
        bunkerGame.setEat(randomItem(bunker.getEats()));
        bunkerGame.setMonths(generateNumberBySplitNumber(bunker.getMonths()) + " Месяца");
        bunkerGame.setLive(randomItem(bunker.getLives()));

        return bunkerGame;
    }

    public String generateDisaster() {
        var bunkerGameModel = readBunker();
        return randomItem(bunkerGameModel.getBunker().getDisasters());
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    @SneakyThrows
    private BunkerGameModel readBunker() {
        Gson gson = new GsonBuilder().create();
        var list = Files.readAllLines(new File(System.getProperty("user.dir") + "/bunker.json").toPath());
        var strokes = String.join("\n", list);
        return gson.fromJson(strokes, BunkerGameModel.class);
    }

    private Bunker generateBunker(BunkerGameModel bunkerGameModel, int size) {
        var bunkerGame = new Bunker();
        var bunker = bunkerGameModel.getBunker();

        bunkerGame.setArea(randomItem(bunker.getAreas()));
        bunkerGame.setAdditionalItems(randomList(bunker.getAdditionalItems(), random(4)));
        bunkerGame.setDescription(randomItem(bunker.getDescriptions()));
        bunkerGame.setDisaster(randomItem(bunker.getDisasters()));
        bunkerGame.setRooms(randomList(bunker.getRooms(), random(4)));
        bunkerGame.setEat(randomItem(bunker.getEats()));
        bunkerGame.setMonths(generateNumberBySplitNumber(bunker.getMonths()) + " Месяца(ев)");
        bunkerGame.setLive(randomItem(bunker.getLives()));

        bunkerGame.setCharacterList(generateCharacters(bunkerGameModel.getCharacter(), size));
        return bunkerGame;
    }

    private List<Character> generateCharacters(CharacterModel characterModel, int size) {
        List<Character> characters = new ArrayList<>();
        List<String> baggage = randomList(characterModel.getBaggage(), size);
        List<String> actions = randomList(characterModel.getActions(), size * 2);
        List<String> jobs = randomList(characterModel.getJobs(), size);
        List<String> additionalInformation = randomList(characterModel.getAdditionalInformation(), size);
        List<String> healths = randomList(characterModel.getHealths(), size);
        List<String> hobbes = randomList(characterModel.getHobbes(), size);
        List<String> humanTrails = randomList(characterModel.getHumanTraits(), size);
        List<String> phobias = randomList(characterModel.getPhobias(), size);

        for (int player = 0, cartsIndex = 0; player < size; player++, cartsIndex += 2) {
            characters.add(generateCharacter(characterModel, baggage.get(player), jobs.get(player),
                    additionalInformation.get(player), healths.get(player), hobbes.get(player), humanTrails.get(player),
                    phobias.get(player), actions.get(cartsIndex), actions.get(cartsIndex + 1)));
        }
        return characters;
    }

    private Character generateCharacter(CharacterModel characterModel, String baggage, String job,
                                        String additionalInformation, String health, String hobby, String humanTrait,
                                        String phobia, String firstAction, String secondAction) {
        var character = new Character();
        character.setBaggage(baggage);
        character.setJob(job + " (" + randomItem(characterModel.getJobStages())+ ")");
        character.setAdditionalInformation(additionalInformation);
        character.setHealth(health + " (Тяжесть: " + random(10, 101) + "%)");
        character.setHobby(hobby + " (" + randomItem(characterModel.getHobbyStages())+")");
        character.setHumanTrait(humanTrait);
        character.setPhobia(phobia);
        character.setFirstCart(firstAction);
        character.setSecondCart(secondAction);
        character.setAge(generateNumberBySplitNumberMany(characterModel.getAges()));
        character.setMaleAngAge(randomItem(characterModel.getMales()) + ", Возраст: " + character.getAge()
                + (new SecureRandom().nextBoolean() ? " (Чайлдфри)" : " (Не чайлдфри)"));
        return character;
    }

    private String generateNumberBySplitNumberMany(String ages) {
        String[] splited = ages.split(",");
        return randomItem(splited);
    }
    private String generateNumberBySplitNumber(String months) {
        String[] splited = months.split("-");
        return String.valueOf(random(Integer.valueOf(splited[0]), Integer.valueOf(splited[1]) + 1));
    }

    private String randomItem(String[] items) {
        return randomList(items, 1).get(0);
    }

    private List<String> randomList(String[] items, Integer bound) {
        List<String> randomItems = new ArrayList<>();
        List<Integer> findedItems = new ArrayList<>();
        for (Integer i = 0; i < bound; i++) {
            var number = random(0, items.length);
            while (findedItems.contains(number)) {
                number = random(0, items.length);
            }
            findedItems.add(number);
            randomItems.add(items[number]);
        }
        return randomItems;
    }

    private int random(int bound) {
        return random(2, bound);
    }

    private int random(int min, int bound) {
        return Math.max(min, new SecureRandom().nextInt(bound));
    }
}
