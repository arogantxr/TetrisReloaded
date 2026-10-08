package htl.steyr.tetris;

import javafx.scene.paint.Color;

import java.util.Random;

// Repräsentiert einen einzelnen Tetris-Stein (Tetromino).
// Speichert seine aktuelle Form (4x4 Matrix), seine Farbe und ob er ein T-Stein ist.
public class Pieces {

    // Alle 7 Standard-Tetromino-Formen, jeweils als 4x4-Matrix (1 = belegt, 0 = leer)
    private static final int[][][] SHAPES = {
            {{0,0,0,0},{1,1,1,1},{0,0,0,0},{0,0,0,0}}, // I
            {{0,0,0,0},{0,1,1,0},{0,1,1,0},{0,0,0,0}}, // O
            {{0,1,0,0},{1,1,1,0},{0,0,0,0},{0,0,0,0}}, // T
            {{0,1,1,0},{1,1,0,0},{0,0,0,0},{0,0,0,0}}, // S
            {{1,1,0,0},{0,1,1,0},{0,0,0,0},{0,0,0,0}}, // Z
            {{1,0,0,0},{1,1,1,0},{0,0,0,0},{0,0,0,0}}, // J
            {{0,0,1,0},{1,1,1,0},{0,0,0,0},{0,0,0,0}}  // L
    };

    // Eine Farbe pro Form, gleiche Reihenfolge wie SHAPES
    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN,
            Color.RED, Color.BLUE, Color.ORANGE
    };

    private static final Random random = new Random();
    public static final int T_INDEX = 2; // Index des T-Steins in SHAPES/COLORS, wird für T-Spin-Check gebraucht

    private int[][] shape;     // aktuelle Form (verändert sich beim Drehen)
    private final Color color; // Farbe bleibt über die Lebenszeit des Steins gleich
    private final boolean isT; // true, wenn dieser Stein ein T-Stein ist (für T-Spin-Erkennung)

    public Pieces(int[][] shape, Color color, boolean isT) {
        this.shape = shape;
        this.color = color;
        this.isT = isT;
    }

    // Erzeugt einen zufälligen neuen Stein aus den 7 möglichen Formen
    public static Pieces random() {
        int index = random.nextInt(SHAPES.length);
        return new Pieces(SHAPES[index], COLORS[index], index == T_INDEX);
    }

    public boolean isT() {
        return isT;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    // Dreht die aktuelle Form um 90° (im oder gegen den Uhrzeigersinn) und gibt
    // die neue Matrix zurück, OHNE die eigene shape zu verändern.
    // Der Aufrufer (GameController) prüft erst per Board.canPlace(), ob die Drehung erlaubt ist,
    // bevor er sie tatsächlich mit setShape() übernimmt.
    public int[][] rotated(boolean clockwise) {
        int[][] result = new int[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (clockwise) {
                    result[c][3 - r] = shape[r][c];
                } else {
                    result[3 - c][r] = shape[r][c];
                }
            }
        }
        return result;
    }

    // Übernimmt eine neue Form (z.B. nach erfolgreicher Drehung)
    public void setShape(int[][] shape) {
        this.shape = shape;
    }
}