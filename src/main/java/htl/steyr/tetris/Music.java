package htl.steyr.tetris;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// Eigene Klasse für die Hintergrundmusik.
// Singleton-Pattern: es gibt über die ganze App nur EINE Instanz (getInstance()),
// damit nicht aus jedem Screen ein eigener MediaPlayer gestartet wird.
public class Music {

    private static Music instance; // die einzige Instanz dieser Klasse

    // Pfade zu den 3 Musikdateien im resources-Ordner
    private final String[] tracks = {
            "/htl/steyr/tetris/music/music1.mp3",
            "/htl/steyr/tetris/music/music2.mp3",
            "/htl/steyr/tetris/music/music3.mp3"
    };

    private int currentTrack = 0;   // Index des aktuell gespielten Songs
    private MediaPlayer player;     // JavaFX-Klasse, die die eigentliche Audiowiedergabe übernimmt
    private double volume = 0.5;    // Lautstärke 0.0 bis 1.0 (JavaFX erwartet diesen Bereich)
    private boolean started = false; // verhindert, dass die Musik mehrfach gestartet wird

    // privater Konstruktor, damit man Music nicht von außen mit "new Music()" erzeugen kann
    private Music() {
    }

    // Zugriff auf die einzige Instanz. Wird beim ersten Aufruf erzeugt, danach wiederverwendet.
    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    // Startet die Musikwiedergabe einmalig (z.B. beim Einloggen im Menü).
    // Wiederholte Aufrufe haben keine Wirkung, solange schon gestartet wurde.
    public void start() {
        if (started) return;
        started = true;
        play();
    }

    // Lädt den aktuellen Track und spielt ihn ab.
    // Wird intern von start(), next() und previous() aufgerufen.
    private void play() {
        if (player != null) {
            player.stop(); // alten Player stoppen, bevor ein neuer Track geladen wird
        }

        Media media = new Media(getClass().getResource(tracks[currentTrack]).toExternalForm());
        player = new MediaPlayer(media);
        player.setVolume(volume);
        player.setOnEndOfMedia(this::next); // wenn der Song zu Ende ist, automatisch zum nächsten springen
        player.play();
    }

    // Springt zum nächsten Song in der Liste (mit Wraparound am Ende)
    public void next() {
        currentTrack = (currentTrack + 1) % tracks.length;
        play();
    }

    // Springt zum vorherigen Song in der Liste (mit Wraparound am Anfang)
    public void previous() {
        currentTrack = (currentTrack - 1 + tracks.length) % tracks.length;
        play();
    }

    // Setzt die Lautstärke. percent kommt vom Options-Slider (0-100), wird hier auf 0.0-1.0 umgerechnet.
    public void setVolume(double percent) {
        this.volume = percent / 100.0;
        if (player != null) {
            player.setVolume(volume);
        }
    }
}