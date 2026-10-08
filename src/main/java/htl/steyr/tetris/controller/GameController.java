package htl.steyr.tetris.controller;

import htl.steyr.tetris.Board;
import htl.steyr.tetris.Pieces;
import htl.steyr.tetris.Score;
import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// Controller für den eigentlichen Spiel-Screen (game.fxml).
// Verbindet die FXML-Elemente mit der Spiellogik aus Board, Pieces und Score.
// Enthält selbst keine Spielregeln mehr, sondern delegiert an die ausgelagerten Klassen.
public class GameController {

    // FXML-Elemente, die per fx:id aus game.fxml gebunden werden
    @FXML
    private GridPane gameField;   // das Spielfeld-Raster
    @FXML
    private Label scoreLabel;
    @FXML
    private Label linesLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Pane holdPane;        // Vorschau-Feld für den gehaltenen Stein
    @FXML
    private Pane nextPane;        // Vorschau-Feld für den nächsten Stein
    @FXML
    private Button pauseButton;

    private static final int CELL_SIZE = 25; // Pixelgröße einer einzelnen Spielfeldzelle

    // Visuelle Darstellung des Spielfelds: ein Rectangle pro Zelle, wird in render() neu eingefärbt
    private final Rectangle[][] cells = new Rectangle[Board.ROWS][Board.COLS];
    private final Board board = new Board(); // die eigentliche Spiellogik des Feldes

    private UserData ud; // aktueller eingeloggter User (für Settings/Tastenbelegung/Highscore)

    private Pieces current; // der aktuell fallende Stein
    private Pieces next;    // Vorschau auf den nächsten Stein
    private Pieces hold;    // der gerade gehaltene Stein (kann nichts oder ein Stein sein)
    private boolean holdUsed = false; // verhindert mehrfaches Holden in derselben Runde

    private int blockRow, blockCol; // Position der oberen linken Ecke des 4x4-Rasters von "current"

    private Timeline timeline;  // steuert das automatische Fallen des Steins
    private boolean paused = false;

    private int score = 0;
    private int lines = 0;
    private int level = 1;

    private final Score scoreCalc = new Score(); // berechnet Punktewerte
    private boolean lastMoveWasRotate = false;   // wird für die T-Spin-Erkennung gebraucht

    // Wird automatisch von JavaFX aufgerufen, sobald game.fxml geladen ist
    @FXML
    public void initialize() {
        ud = UserSession.getUserData();
        buildGrid(); // visuelles Raster aus Rectangles erzeugen

        next = Pieces.random(); // ersten "next"-Stein vorbereiten
        spawnPiece();           // den ersten echten Stein spawnen

        // WICHTIG: Listener für die Preview-Pane
        nextPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateNextPreview();
        });

        nextPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateNextPreview();
        });

        // Ersten Preview korrekt zeichnen (nach Layout-Pass)
        Platform.runLater(this::updateNextPreview);

        // Tastatursteuerung kann erst gesetzt werden, sobald die Scene existiert
        // (beim ersten initialize() ist die Scene oft noch null)
        gameField.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                gameField.setFocusTraversable(true);
                Platform.runLater(() -> gameField.requestFocus());
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, this::handleKey);
            }
        });

        startTimeline(600); // Stein fällt anfangs alle 600ms eine Reihe
        render();
    }

    // Erstellt einmalig das 20x10-Raster aus Rectangle-Objekten und fügt sie ins GridPane ein
    private void buildGrid() {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Rectangle r = new Rectangle(CELL_SIZE, CELL_SIZE);
                r.setFill(Color.web("#0A1628"));
                r.setStroke(Color.web("#17263C"));
                r.setStrokeWidth(1);
                cells[row][col] = r;
                gameField.add(r, col, row);
            }
        }
    }

    // (Neu)Startet die Fall-Timeline mit einer bestimmten Geschwindigkeit (in Millisekunden pro Reihe).
    // Wird bei Levelaufstieg aufgerufen, um das Spiel schneller zu machen.
    private void startTimeline(double millis) {
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(millis), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Zentrale Tastatur-Eingabeverarbeitung. Liest die individuellen Tastenbelegungen
    // aus UserData aus (siehe Options-Screen) statt fix verdrahteter Tasten.
    private void handleKey(javafx.scene.input.KeyEvent event) {
        event.consume();
        if (paused) return;

        KeyCode code = event.getCode();

        if (code == ud.getSetting("left")) {
            move(0, -1);
        } else if (code == ud.getSetting("right")) {
            move(0, 1);
        } else if (code == ud.getSetting("down") || code == ud.getSetting("softdrop")) {
            if (move(1, 0)) {
                score += scoreCalc.softDrop(1); // Punkte für manuelles schnelleres Fallen
                scoreLabel.setText(String.format("%03d", score));
            } else {
                lockPiece(); // kann nicht mehr weiter fallen -> sofort einfrieren
            }
        } else if (code == ud.getSetting("rotate_left")) {
            rotate(false);
        } else if (code == ud.getSetting("rotate_right")) {
            rotate(true);
        } else if (code == ud.getSetting("hold")) {
            holdCurrent();
        } else if (code == ud.getSetting("harddrop")) {
            hardDrop();
        } else if (code.toString().equals("ESCAPE")){
            onPauseButtonClicked(new ActionEvent());
        }

        render(); // nach jeder Eingabe das Spielfeld neu zeichnen
    }

    // Wird von der Timeline regelmäßig aufgerufen (automatisches Fallen)
    private void tick() {
        if (paused) return;
        if (!move(1, 0)) lockPiece(); // kann nicht weiter fallen -> locken
        render();
    }

    // Setzt einen neuen aktuellen Stein, bereitet den nächsten vor und prüft auf Game Over
    private void spawnPiece() {
        current = next;
        next = Pieces.random();

        blockRow = 0;
        blockCol = Board.COLS / 2 - 2; // mittig oben starten
        holdUsed = false;

        // Wenn der neue Stein gleich an seiner Startposition nicht platziert werden kann,
        // ist das Spielfeld voll -> Game Over
        if (!board.canPlace(current.getShape(), blockRow, blockCol)) {
            gameOver();
        }

        updateNextPreview(); // "Next"-Vorschau aktualisieren (oder leer lassen, je nach Setting)
    }

    // Versucht den aktuellen Stein um (dRow, dCol) zu verschieben.
    // Gibt true zurück, wenn die Bewegung erlaubt war, sonst false (z.B. Kollision).
    private boolean move(int dRow, int dCol) {
        int newRow = blockRow + dRow;
        int newCol = blockCol + dCol;

        if (board.canPlace(current.getShape(), newRow, newCol)) {
            blockRow = newRow;
            blockCol = newCol;
            lastMoveWasRotate = false; // letzte Aktion war eine Bewegung, kein Dreh -> für T-Spin relevant
            return true;
        }
        return false;
    }

    // Dreht den aktuellen Stein, wenn die gedrehte Form an der aktuellen Position erlaubt ist
    private void rotate(boolean clockwise) {
        int[][] rotated = current.rotated(clockwise);
        if (board.canPlace(rotated, blockRow, blockCol)) {
            current.setShape(rotated);
            lastMoveWasRotate = true; // wird für die T-Spin-Erkennung beim Locken gebraucht
        }
    }

    // Lässt den Stein sofort bis zur Kollision nach unten fallen ("Hard Drop")
    private void hardDrop() {
        int rows = 0;
        while (move(1, 0)) {
            rows++; // zählt, wie viele Reihen tatsächlich gefallen sind, für die Punkteberechnung
        }
        score += scoreCalc.hardDrop(rows);
        scoreLabel.setText(String.format("%03d", score));
        lockPiece();
    }

    // Tauscht den aktuellen Stein mit dem gehaltenen Stein (einmal pro Spawn erlaubt)
    private void holdCurrent() {
        if (holdUsed) return;
        holdUsed = true;

        if (hold == null) {
            // noch nichts gehalten -> aktuellen Stein "parken" und neuen spawnen
            hold = current;
            spawnPiece();
        } else {
            // schon ein Stein gehalten -> tauschen
            Pieces temp = current;
            current = hold;
            hold = temp;
            blockRow = 0;
            blockCol = Board.COLS / 2 - 2;
        }

        drawPreview(holdPane, hold); // "Hold"-Vorschau aktualisieren
    }

    // Friert den aktuellen Stein im Spielfeld ein, berechnet Punkte, prüft Level-Aufstieg
    // und spawnt danach den nächsten Stein.
    private void lockPiece() {
        // T-Spin liegt vor, wenn: Stein ist ein T, letzte Aktion war eine Drehung,
        // UND die 3-Corner-Regel im Board erfüllt ist
        boolean tSpin = current.isT() && lastMoveWasRotate && board.isTSpin(blockRow, blockCol);

        board.lock(current.getShape(), blockRow, blockCol, current.getColor());

        int cleared = board.clearFullLines();

        int gained = 0;
        if (tSpin) {
            gained = scoreCalc.tSpin(cleared);
        } else if (cleared > 0) {
            gained = scoreCalc.linesCleared(cleared);
        }

        if (gained > 0) {
            score += gained * level; // höheres Level = mehr Punkte pro Clear
        }

        if (cleared > 0) {
            lines += cleared;
            level = 1 + lines / 10; // alle 10 Reihen ein Level höher
            startTimeline(Math.max(150, 600 - (level - 1) * 50)); // Spiel wird mit jedem Level schneller (min. 150ms)
        }

        scoreLabel.setText(String.format("%03d", score));
        linesLabel.setText(String.format("%02d", lines));
        levelLabel.setText(String.format("%02d", level));

        lastMoveWasRotate = false;
        spawnPiece();
    }

    // Zeichnet das gesamte Spielfeld neu: zuerst das gelockte Board, dann der fallende Stein darüber
    private void render() {
        // Hintergrund: alle bereits gelockten Blöcke aus dem Board-Array
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Color c = board.get(row, col);
                cells[row][col].setFill(c != null ? c : Color.web("#0A1628"));
            }
        }

        // Vordergrund: der aktuell fallende Stein wird über das Board gelegt
        int[][] shape = current.getShape();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 1) {
                    int boardRow = blockRow + r;
                    int boardCol = blockCol + c;
                    if (boardRow >= 0 && boardRow < Board.ROWS && boardCol >= 0 && boardCol < Board.COLS) {
                        cells[boardRow][boardCol].setFill(current.getColor());
                    }
                }
            }
        }
    }

    // Zeichnet die "Next"-Vorschau, sofern der User sie in den Einstellungen aktiviert hat.
    // Ist "Blind-Modus" aktiv (showNextPiece == false), bleibt die Pane leer.
    private void updateNextPreview() {
        if (ud.isShowNextPiece()) {
            drawPreview(nextPane, next);
        } else {
            nextPane.getChildren().clear();
        }
    }

    // Zeichnet eine kleine 4x4-Vorschau eines Steins (für Hold- und Next-Pane)
    private void drawPreview(Pane pane, Pieces piece) {
        pane.getChildren().clear();

        int size = 18;
        int[][] shape = piece.getShape();

        // Bounding Box berechnen
        int minRow = 4, maxRow = -1;
        int minCol = 4, maxCol = -1;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    minRow = Math.min(minRow, r);
                    maxRow = Math.max(maxRow, r);
                    minCol = Math.min(minCol, c);
                    maxCol = Math.max(maxCol, c);
                }
            }
        }

        int blockWidth = (maxCol - minCol + 1) * size;
        int blockHeight = (maxRow - minRow + 1) * size;

        // Zentrier-Offset
        double offsetX = (pane.getWidth() - blockWidth) / 2;
        double offsetY = (pane.getHeight() - blockHeight) / 2;

        // Zeichnen mit Offset
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    Rectangle rect = new Rectangle(size, size);
                    rect.setFill(piece.getColor());

                    rect.setX(offsetX + (c - minCol) * size);
                    rect.setY(offsetY + (r - minRow) * size);

                    pane.getChildren().add(rect);
                }
            }
        }
    }


    // Wird aufgerufen, wenn ein neuer Stein nicht mehr platziert werden kann (Spielfeld voll).
    // Speichert den Score im UserData und wechselt zum Gameover-Screen.
    private void gameOver() {
        timeline.stop();
        ud.setScore(score);
        ud.save();
        ViewSwitcher.switchTo("gameover.fxml");
    }

    // Button oben rechts: Spiel beenden
    public void onCloseButtonClicked(ActionEvent actionEvent) {
        ud.save();
        ViewSwitcher.switchTo("menu.fxml");
    }

    // Pause-Button: stoppt/startet die Eingabeverarbeitung und das Fallen,
    // ändert das Symbol auf dem Button entsprechend
    public void onPauseButtonClicked(ActionEvent actionEvent) {
        paused = !paused;
        pauseButton.setText(paused ? "▶" : "⏸");
        gameField.requestFocus(); // Fokus zurück aufs Spielfeld, damit Tastatureingaben weiter ankommen
    }
}