package htl.steyr.tetris.highscore_management;

import com.google.gson.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class HighscoreManager {

    private static final Path FILE = Paths.get("data", "highscores.txt");
    private static final int MAX_SCORES = 10;

    public static void writeHighscore(Score score) {
        List<Score> scores = loadHighscores();

        if (scores.size() < MAX_SCORES || score.value() > scores.get(scores.size() - 1).value()) {
            scores.add(score);
            scores.sort((a, b) -> b.value() - a.value());
            if (scores.size() > MAX_SCORES) {
                scores.remove(scores.size() - 1);
            }
            save(scores);
        }
    }

    public static List<Score> loadHighscores() {
        if (!Files.exists(FILE)) {
            return new ArrayList<>();
        }

        try {
            JsonArray array = JsonParser.parseString(Files.readString(FILE)).getAsJsonArray();
            List<Score> scores = new ArrayList<>();
            for (JsonElement e : array) {
                JsonObject obj = e.getAsJsonObject();
                scores.add(new Score(obj.get("name").getAsString(), obj.get("value").getAsInt()));
            }
            return scores;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static void save(List<Score> scores) {
        JsonArray array = new JsonArray();
        for (Score score : scores) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", score.name());
            obj.addProperty("value", score.value());
            array.add(obj);
        }

        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, new GsonBuilder().setPrettyPrinting().create().toJson(array));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearData() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonArray empty = new JsonArray();
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(empty);
            Files.writeString(FILE, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}