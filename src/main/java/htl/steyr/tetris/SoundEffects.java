package htl.steyr.tetris;

import javafx.scene.media.AudioClip;

import java.net.URL;

public final class SoundEffects {
    private static final AudioClip PLACE = load("/htl/steyr/tetris/sounds/place.mp3");
    // Sound für das Löschen einer oder mehrerer vollständiger Reihen.
    private static final AudioClip LINE_CLEARED = load("/htl/steyr/tetris/sounds/tetrislineclear.mp3");
    private static double volume = 0.5;

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

    // Spielt den Line-Clear-Sound nur ab, wenn die Sounddatei geladen werden konnte.
    public static void playLineClear() {
        if (LINE_CLEARED != null) {
            LINE_CLEARED.play();
        }
    }

    // Übernimmt die SFX-Sliderlautstärke (0-100) für alle Soundeffekte.
    public static void setVolume(double percent) {
        volume = percent / 100.0;
        if (PLACE != null) {
            PLACE.setVolume(volume);
        }
        if (LINE_CLEARED != null) {
            LINE_CLEARED.setVolume(volume);
        }
    }
}
