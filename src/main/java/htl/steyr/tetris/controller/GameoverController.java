package htl.steyr.tetris.controller;

import htl.steyr.tetris.utility.ViewSwitcher;
import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import htl.steyr.tetris.highscore_management.HighscoreManager;
import htl.steyr.tetris.highscore_management.Score;
import java.time.LocalDateTime;

// Controller für den Gameover-Screen. Zeigt den erreichten Score, vergleicht ihn mit dem
// persönlichen Highscore und trägt ihn in die globale Highscore-Liste ein.
public class GameoverController {

    @FXML
    private Label scoreLabel;
    @FXML
    private Label highscoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label linesLabel;
    @FXML
    private Label newHighscoreBadge; // nur sichtbar, wenn der alte Highscore geschlagen wurde
    @FXML
    private Button retryButton;
    @FXML
    private Button menuButton;

    private int score;

    private UserData ud;

    // Wird automatisch beim Laden von gameover.fxml aufgerufen
    public void initialize() {
        retryButton.setOnAction(e -> restartGame());
        menuButton.setOnAction(e -> returnToMenu());
        ud = UserSession.getUserData();
        setScore(ud.getScore()); // Score wurde vorher im GameController in UserData gespeichert
    }

    // Verarbeitet den finalen Score: Anzeige, persönlicher Highscore-Vergleich,
    // und Eintrag in die globale Highscore-Liste (highscore_management)
    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText(String.valueOf(score));

        UserData ud = UserSession.getUserData();
        int oldHighscore = ud.getHighscore();

        // neuer persönlicher Highscore? -> direkt übernehmen
        boolean newHighscore = score > oldHighscore;
        if (newHighscore) {
            ud.setHighscore(score);
            oldHighscore = score;
        }

        highscoreLabel.setText(String.valueOf(oldHighscore));
        levelLabel.setText(String.format("%02d", ud.getLevel()));
        linesLabel.setText(String.format("%02d", ud.getLines()));
        newHighscoreBadge.setVisible(newHighscore);
        newHighscoreBadge.setManaged(newHighscore);

        // Score zusätzlich in die globale Top-10-Liste eintragen (wird im Menü angezeigt)
        HighscoreManager.writeHighscore(new Score(
                ud.getUsername(), score, LocalDateTime.now(), ud.getLevel(), ud.getLines()));
    }

    // "Retry"-Button: direkt eine neue Runde starten
    private void restartGame() {
        ud.save();
        ViewSwitcher.switchTo("game.fxml");
    }

    // "Menu"-Button: zurück zum Hauptmenü
    private void returnToMenu() {
        ud.save();
        ViewSwitcher.switchTo("menu.fxml");
    }
}