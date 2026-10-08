package htl.steyr.tetris;

import javafx.scene.paint.Color;

// Verwaltet das eigentliche Spielfeld (10x20 Felder).
// Jedes Feld speichert entweder null (leer) oder eine Color (belegt, vom gelockten Stein).
public class Board {

    public static final int ROWS = 20; // Anzahl Reihen im Spielfeld
    public static final int COLS = 10; // Anzahl Spalten im Spielfeld

    // Das eigentliche Spielfeld-Array. grid[row][col] = Farbe des Blocks oder null wenn leer
    private final Color[][] grid = new Color[ROWS][COLS];

    // Gibt die Farbe an einer bestimmten Position zurück (für das Rendering im GameController)
    public Color get(int row, int col) {
        return grid[row][col];
    }

    // Prüft, ob ein Stein (4x4 Matrix) an einer bestimmten Position platziert werden darf.
    // Wird sowohl beim Bewegen als auch beim Drehen aufgerufen, um Kollisionen zu verhindern.
    public boolean canPlace(int[][] shape, int newRow, int newCol) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 1) { // nur belegte Zellen des Steins prüfen
                    int boardRow = newRow + r;
                    int boardCol = newCol + c;

                    // außerhalb des Spielfelds (links/rechts/unten)?
                    if (boardCol < 0 || boardCol >= COLS || boardRow >= ROWS) {
                        return false;
                    }
                    // Kollision mit einem schon gelockten Block?
                    // (boardRow < 0 wird ignoriert, weil der Stein oben noch teilweise außerhalb sein darf, bevor er spawnt)
                    if (boardRow >= 0 && grid[boardRow][boardCol] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // "Friert" den aktuellen Stein im Spielfeld ein, sobald er nicht mehr weiter fallen kann.
    // Trägt die Farbe des Steins fest ins grid-Array ein.
    public void lock(int[][] shape, int row, int col, Color color) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 1) {
                    int boardRow = row + r;
                    int boardCol = col + c;
                    if (boardRow >= 0 && boardRow < ROWS && boardCol >= 0 && boardCol < COLS) {
                        grid[boardRow][boardCol] = color;
                    }
                }
            }
        }
    }

    // Geht das Spielfeld von unten nach oben durch und entfernt jede volle Reihe.
    // Volle Reihen werden gelöscht, alles darüber rutscht eine Reihe nach unten.
    // Gibt zurück, wie viele Reihen gleichzeitig gelöscht wurden (wichtig für die Punkteberechnung).
    public int clearFullLines() {
        int clearedLines = 0;

        for (int row = ROWS - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == null) {
                    full = false; // sobald eine Lücke gefunden wird, ist die Reihe nicht voll
                    break;
                }
            }

            if (full) {
                clearedLines++;
                // alles oberhalb dieser Reihe um 1 nach unten kopieren ("fallen lassen")
                for (int r = row; r > 0; r--) {
                    grid[r] = grid[r - 1].clone();
                }
                grid[0] = new Color[COLS]; // oberste Reihe wird wieder komplett leer
                row++; // gleiche Reihenposition nochmal prüfen, da jetzt eine neue Reihe nachgerutscht ist
            }
        }
        return clearedLines;
    }

    // Vereinfachte T-Spin-Erkennung (3-Corner-Regel):
    // Wenn mindestens 3 der 4 diagonalen Eckfelder rund um den Mittelpunkt des T-Steins
    // belegt sind (oder außerhalb des Feldes liegen), gilt das Drehen als T-Spin.
    public boolean isTSpin(int row, int col) {
        // Mittelpunkt des T-Steins liegt im 4x4-Raster bei (1,1)
        int centerRow = row + 1;
        int centerCol = col + 1;

        int[][] corners = {
                {centerRow - 1, centerCol - 1}, // oben links
                {centerRow - 1, centerCol + 1}, // oben rechts
                {centerRow + 1, centerCol - 1}, // unten links
                {centerRow + 1, centerCol + 1}  // unten rechts
        };

        int filled = 0;
        for (int[] corner : corners) {
            int r = corner[0];
            int c = corner[1];
            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || grid[r][c] != null) {
                filled++;
            }
        }
        return filled >= 3;
    }
}