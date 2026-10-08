package htl.steyr.tetris.utility;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Hasher {
    public static String hashText(String text) {
        if (!text.isEmpty()) {
            String hashedText = Hashing.sha256()
                    .hashString(text, StandardCharsets.UTF_8)
                    .toString();

            return hashedText;
        }
        throw new IllegalArgumentException("Die Variable 'text' darf nicht leer bleiben.");
    }
}

