package htl.steyr.tetris;

import javafx.scene.media.AudioClip;

import java.net.URL;

public final class SoundEffects {
    private static final AudioClip PLACE = load("/htl/steyr/tetris/sounds/place.mp3");

    private SoundEffects() {}

    // Lädt einen Sound aus den Resources. Fehlt die Datei, wird null zurückgegeben,
    // damit das Spiel ohne Sound weiterläuft statt abzustürzen.
    private static AudioClip load(String path) {
        URL url = SoundEffects.class.getResource(path);
        if (url == null) {
            System.err.println("Sounddatei nicht gefunden: " + path);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

    public static void playPlace() {
        if (PLACE != null) {
            PLACE.play();
        }
    }
}
