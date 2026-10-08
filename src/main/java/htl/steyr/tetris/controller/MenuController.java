package htl.steyr.tetris.controller;

import htl.steyr.tetris.highscore_management.HighscoreManager;
import htl.steyr.tetris.highscore_management.Score;
import htl.steyr.tetris.user.UserData;
import htl.steyr.tetris.user.UserSession;
import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

// Controller für das Hauptmenü. Zeigt die Top-10-Highscores, begrüßt den eingeloggten
// User und startet hier auch die Hintergrundmusik (erster Screen nach dem Login).
public class MenuController {


    @FXML
    private Button closeButton;
    @FXML
    private Button playButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<Score> highscoreList; // zeigt die geladenen Highscores an
    @FXML
    private Pane adBanner;

    private UserData ud;

    public void initialize(){
        // bestehende Highscores aus der Datei laden und in die Liste eintragen
        highscoreList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Score score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("%02d   %-12s   %,d", getIndex() + 1, score.name(), score.value()));
                }
            }
        });
        highscoreList.getItems().addAll(HighscoreManager.loadHighscores());
        ud = UserSession.getUserData();

        //Fehler mit Ki behoben
        if (ud != null) {
            welcomeLabel.setText("Willkommen " + ud.getUsername());
            // Musiklautstärke aus den gespeicherten Usereinstellungen übernehmen
            htl.steyr.tetris.Music.getInstance().setVolume(ud.getVolumeMusic());
        } else {
            welcomeLabel.setText("Willkommen Spieler");
        }

        // Musik starten (passiert nur einmal, weitere Aufrufe werden in Music.start() ignoriert)
        htl.steyr.tetris.Music.getInstance().start();
    }

    public void onClosedButtonClicked(ActionEvent actionEvent) {
        ud.save();
        Platform.exit();
    }

    public void onPlayButtonClicked(ActionEvent actionEvent) {
        ud.save();
        ViewSwitcher.switchTo("game.fxml");
    }

    public void onOptionsButtonClicked(ActionEvent actionEvent) {
        ud.save();
        ViewSwitcher.switchTo("options.fxml");
    }
}