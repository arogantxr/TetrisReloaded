package htl.steyr.tetris;

// Reine Punkteberechnung. Diese Klasse kennt nichts vom Spielfeld oder den Steinen,
// sie bekommt nur Zahlen (wie viele Reihen, wie viele Felder gefallen) und gibt Punkte zurück.
public class Score {

    // Punkte für normale Zeilen-Clears (abhängig davon, wie viele gleichzeitig gelöscht wurden)
    private static final int SINGLE = 100;  // 1 Reihe
    private static final int DOUBLE = 300;  // 2 Reihen
    private static final int TRIPLE = 500;  // 3 Reihen
    private static final int TETRIS = 800;  // 4 Reihen (= "Tetris")

    // Punkte für T-Spins (Drehung des T-Steins direkt vor dem Locken, siehe Board.isTSpin)
    private static final int TSPIN = 400;         // T-Spin ohne oder mit 1 gelöschten Reihe
    private static final int TSPIN_DOUBLE = 1200; // T-Spin mit 2 gelöschten Reihen
    private static final int TSPIN_TRIPLE = 1600; // T-Spin mit 3 gelöschten Reihen

    // Punkte pro gefallener Reihe bei Soft-/Hard-Drop
    private static final int SOFT_DROP_PER_ROW = 1;
    private static final int HARD_DROP_PER_ROW = 2;

    // Gibt die Punkte für einen normalen Zeilen-Clear zurück (kein T-Spin)
    public int linesCleared(int lines) {
        switch (lines) {
            case 1: return SINGLE;
            case 2: return DOUBLE;
            case 3: return TRIPLE;
            case 4: return TETRIS;
            default: return 0; // 0 Reihen gelöscht = keine Punkte
        }
    }

    // Gibt die Punkte für einen T-Spin zurück, abhängig davon wie viele Reihen dabei gelöscht wurden
    public int tSpin(int lines) {
        switch (lines) {
            case 0: return TSPIN; // T-Spin ohne Zeilenlöschung zählt trotzdem
            case 1: return TSPIN;
            case 2: return TSPIN_DOUBLE;
            case 3: return TSPIN_TRIPLE;
            default: return 0;
        }
    }

    // Punkte für Soft Drop (manuelles schnelleres Runterdrücken), pro gefallener Reihe
    public int softDrop(int rowsFallen) {
        return rowsFallen * SOFT_DROP_PER_ROW;
    }

    // Punkte für Hard Drop (Stein fällt sofort ganz nach unten), pro gefallener Reihe
    public int hardDrop(int rowsFallen) {
        return rowsFallen * HARD_DROP_PER_ROW;
    }
}