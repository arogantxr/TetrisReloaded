package htl.steyr.tetris.highscore_management;

public record Score(String name, int value) {
    public String toString(){
        return name + " | " + value;
    }
}
