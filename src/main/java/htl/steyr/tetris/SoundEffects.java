package htl.steyr.tetris;

import javafx.scene.media.AudioClip;

public final class SoundEffects {
    private static final AudioClip PLACE = new AudioClip(
            SoundEffects.class
                    .getResource("/htl/steyr/tetris/sounds/place.mp3")
                    .toExternalForm()
    );

    private SoundEffects() {}

    public static void playPlace() {
        PLACE.play();
    }
}