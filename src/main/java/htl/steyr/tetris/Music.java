package htl.steyr.tetris;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// Eigene Klasse für die Hintergrundmusik.
// Singleton-Pattern: es gibt über die ganze App nur EINE Instanz (getInstance()),
// damit nicht aus jedem Screen ein eigener MediaPlayer gestartet wird.
//
// NEUE SONGS HINZUFÜGEN: einfach eine .mp3-Datei in den Ordner
// src/main/resources/htl/steyr/tetris/music/ legen. Hier im Code muss NICHTS
// eingetragen werden – die Liste wird beim Start automatisch aus dem Ordner gelesen.
// Der Dateiname (ohne .mp3) wird im Pause-Screen als Songname angezeigt.
public class Music {

    private static Music instance; // die einzige Instanz dieser Klasse

    private static final String MUSIC_DIR = "/htl/steyr/tetris/music/";
    private static final String EXTENSION = ".mp3";

    // Dateinamen aller verfügbaren Songs, z.B. "music1.mp3" (natürlich sortiert: 1, 2, ..., 10)
    private final List<String> tracks = discoverTracks();

    // Index des aktuell gespielten Songs. Als Property, damit z.B. der Pause-Screen
    // mitbekommt, wenn der Song gewechselt wird (z.B. über die Options oder wenn ein kaputter Song übersprungen wird).
    private final ReadOnlyIntegerWrapper currentTrack = new ReadOnlyIntegerWrapper(0);
    private MediaPlayer player;      // JavaFX-Klasse, die die eigentliche Audiowiedergabe übernimmt
    private double volume = 0.5;     // Lautstärke 0.0 bis 1.0 (JavaFX erwartet diesen Bereich)
    private boolean started = false; // verhindert, dass die Musik mehrfach gestartet wird
    private int failedInARow = 0;    // verhindert eine Endlosschleife, falls ALLE Dateien kaputt sind

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

    // ---------------------------------------------------------------------
    // Songs finden
    // ---------------------------------------------------------------------

    // Sucht alle .mp3-Dateien im Musik-Ordner (funktioniert sowohl aus dem
    // target-Ordner als auch aus einer .jar heraus).
    private static List<String> discoverTracks() {
        List<String> found = new ArrayList<>();
        try {
            URL url = Music.class.getResource(MUSIC_DIR);
            if (url != null) {
                URI uri = url.toURI();
                if ("jar".equals(uri.getScheme())) {
                    FileSystem fs;
                    try {
                        fs = FileSystems.newFileSystem(uri, Map.of());
                    } catch (FileSystemAlreadyExistsException e) {
                        fs = FileSystems.getFileSystem(uri);
                    }
                    collectMp3s(fs.getPath(MUSIC_DIR), found);
                } else {
                    collectMp3s(Paths.get(uri), found);
                }
            }
        } catch (Exception e) {
            found.clear();
        }

        // Falls der Ordner nicht gelesen werden konnte: nach "music1.mp3", "music2.mp3", ...
        // suchen, damit die Musik trotzdem funktioniert.
        if (found.isEmpty()) {
            for (int i = 1; i <= 99; i++) {
                String name = "music" + i + EXTENSION;
                if (Music.class.getResource(MUSIC_DIR + name) != null) {
                    found.add(name);
                }
            }
        }

        found.sort(NATURAL_ORDER);
        return Collections.unmodifiableList(found);
    }

    private static void collectMp3s(Path dir, List<String> into) throws Exception {
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.toLowerCase().endsWith(EXTENSION))
                    .forEach(into::add);
        }
    }

    // Sortiert Zahlen im Namen numerisch, damit "music10" nach "music9" kommt
    // und nicht nach "music1" (wie bei rein alphabetischer Sortierung).
    private static final Comparator<String> NATURAL_ORDER = (a, b) -> {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char ca = a.charAt(i), cb = b.charAt(j);
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int si = i, sj = j;
                while (i < a.length() && Character.isDigit(a.charAt(i))) i++;
                while (j < b.length() && Character.isDigit(b.charAt(j))) j++;
                String na = a.substring(si, i).replaceFirst("^0+(?=.)", "");
                String nb = b.substring(sj, j).replaceFirst("^0+(?=.)", "");
                if (na.length() != nb.length()) return na.length() - nb.length();
                int cmp = na.compareTo(nb);
                if (cmp != 0) return cmp;
            } else {
                int cmp = Character.compare(Character.toLowerCase(ca), Character.toLowerCase(cb));
                if (cmp != 0) return cmp;
                i++;
                j++;
            }
        }
        return (a.length() - i) - (b.length() - j);
    };

    // ---------------------------------------------------------------------
    // Wiedergabe
    // ---------------------------------------------------------------------

    // Startet die Musikwiedergabe einmalig (z.B. beim Einloggen im Menü).
    // Wiederholte Aufrufe haben keine Wirkung, solange schon gestartet wurde.
    public void start() {
        if (started) return;
        started = true;
        play();
    }

    // Lädt den aktuellen Track und spielt ihn ab.
    // Kann eine Datei nicht abgespielt werden (kaputt, falsches Format, ...), wird sie
    // übersprungen, statt dass das Programm abstürzt.
    private void play() {
        if (tracks.isEmpty()) return; // keine Songs vorhanden -> einfach keine Musik

        if (player != null) {
            player.stop(); // alten Player stoppen, bevor ein neuer Track geladen wird
            player.dispose();
            player = null;
        }

        try {
            URL resource = getClass().getResource(MUSIC_DIR + tracks.get(currentTrack.get()));
            if (resource == null) throw new IllegalStateException("Song nicht gefunden");

            MediaPlayer newPlayer = new MediaPlayer(new Media(resource.toExternalForm()));
            newPlayer.setVolume(volume);
            newPlayer.setOnPlaying(() -> failedInARow = 0);
            // Der ausgewählte Song wird in Dauerschleife wiederholt, bis ein anderer
            // gewählt wird (Previous/Next oder Klick in der Playlist).
            newPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            // Callbacks kommen vom Player selbst -> Wechsel erst danach ausführen
            newPlayer.setOnError(() -> Platform.runLater(this::skipBrokenTrack));
            player = newPlayer;
            player.play();
        } catch (Exception e) {
            skipBrokenTrack();
        }
    }

    // Überspringt einen Song, der nicht abgespielt werden kann.
    // Sind alle Songs kaputt, wird aufgehört, statt endlos weiterzuspringen.
    private void skipBrokenTrack() {
        System.err.println("Song konnte nicht abgespielt werden: " + tracks.get(currentTrack.get()));
        failedInARow++;
        if (failedInARow >= tracks.size()) return;
        next();
    }

    // Springt zum nächsten Song in der Liste (mit Wraparound am Ende)
    public void next() {
        if (tracks.isEmpty()) return;
        playTrack((currentTrack.get() + 1) % tracks.size());
    }

    // Springt zum vorherigen Song in der Liste (mit Wraparound am Anfang)
    public void previous() {
        if (tracks.isEmpty()) return;
        playTrack((currentTrack.get() - 1 + tracks.size()) % tracks.size());
    }

    // Spielt gezielt einen bestimmten Song aus der Liste ab (z.B. per Klick im Pause-Screen)
    public void playTrack(int index) {
        if (index < 0 || index >= tracks.size()) return;
        currentTrack.set(index);
        started = true;
        play();
    }

    // Anzeigenamen aller Songs, ohne Dateiendung (z.B. "music1")
    public List<String> getTrackNames() {
        List<String> names = new ArrayList<>();
        for (String file : tracks) {
            names.add(file.substring(0, file.length() - EXTENSION.length()));
        }
        return names;
    }

    public ReadOnlyIntegerProperty currentTrackProperty() {
        return currentTrack.getReadOnlyProperty();
    }

    // Setzt die Lautstärke. percent kommt vom Options-Slider (0-100), wird hier auf 0.0-1.0 umgerechnet.
    public void setVolume(double percent) {
        this.volume = percent / 100.0;
        if (player != null) {
            player.setVolume(volume);
        }
    }

    // Aktuelle Lautstärke in Prozent (0-100), passend für einen Slider
    public double getVolume() {
        return volume * 100.0;
    }
}
