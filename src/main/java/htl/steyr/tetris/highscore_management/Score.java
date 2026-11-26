package htl.steyr.tetris.highscore_management;

import java.time.LocalDateTime;

public record Score(String name, int value, LocalDateTime date, int level, int lines) {
    public String toString(){
        return name + " | " + value;
    }
}
