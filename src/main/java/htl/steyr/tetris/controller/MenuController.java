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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Controller für das Hauptmenü. Zeigt die Top-10-Highscores, begrüßt den eingeloggten
// User und startet hier auch die Hintergrundmusik (erster Screen nach dem Login).
public class MenuController {

    @FXML
    private VBox highscorePanel;
    @FXML
    private VBox bannerPanel;
    @FXML
    private StackPane logoContainer;
    @FXML
    private Button closeButton;
    @FXML
    private Button playButton;
    @FXML
    private Button optionsButton;
    @FXML
    private Button viewAllScoresButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<Score> highscoreList; // zeigt die geladenen Highscores an
    @FXML
    private ImageView logoImageView;
    @FXML
    private Pane adBanner;

    private UserData ud;

    public void initialize(){
        String logoPath = ViewSwitcher.isDarkMode()
                ? "/image/HTLSteyr_Logo_White.png"
                : "/image/HTLSteyr_Logo_Blue.png";
        logoImageView.setImage(new Image(Objects.requireNonNull(
                getClass().getResource(logoPath), "Logo resource not found: " + logoPath
        ).toExternalForm()));

        // Logo füllt immer den verfügbaren Platz im Container (Seitenverhältnis bleibt erhalten)
        logoImageView.fitWidthProperty().bind(logoContainer.widthProperty());
        logoImageView.fitHeightProperty().bind(logoContainer.heightProperty());

        // Panelbreite wächst mit dem Fenster mit (ca. 22 % der Breite), damit auch seitlich Platz da ist
        bannerPanel.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                //Responsive Design: Breite der Panels an die Fenstergröße anpassen (22% für Banner, 26% für Highscore)
                bannerPanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.22));
                highscorePanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.26));
            }
        });

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        //Zweizeilige Darstellung der Highscore-Einträge
        highscoreList.setCellFactory(list -> new ListCell<>() {
            //Zeile Oben
            private final Label rankLabel = new Label();
            private final Label nameLabel = new Label();
            private final Region topSpacer = new Region();
            private final Label scoreLabel = new Label();

            //Zeile Unten
            private final Label statsLabel = new Label();
            private final Region bottomSpacer = new Region();
            private final Label dateLabel = new Label();

            //Zeilen zusammenfügen
            private final HBox topRow = new HBox(6, rankLabel, nameLabel, topSpacer, scoreLabel);
            private final HBox bottomRow = new HBox(6, statsLabel, bottomSpacer, dateLabel);
            private final VBox cellBox = new VBox(2, topRow, bottomRow);

            //Instance Initialization Block
            {
                // Spacer dehnen sich aus und drücken Punkte sowie Datum rechtsbündig an den Rand
                HBox.setHgrow(topSpacer, Priority.ALWAYS);
                HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

                //CSS-Klassen
                rankLabel.getStyleClass().add("highscore-rank");
                nameLabel.getStyleClass().add("highscore-main");
                scoreLabel.getStyleClass().add("highscore-main");
                statsLabel.getStyleClass().add("highscore-meta");
                dateLabel.getStyleClass().add("highscore-meta");
            }

            @Override
            protected void updateItem(Score score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //Variablen der Oberen Zeile setzen
                    rankLabel.setText(String.format("%02d", getIndex() + 1));
                    nameLabel.setText(score.name());
                    scoreLabel.setText(String.format("%,d", score.value()));

                    //Variablen der Unteren Zeile setzen
                    statsLabel.setText(String.format("Lvl %d   Lines %d", score.level(), score.lines()));
                    dateLabel.setText(score.date().equals(java.time.LocalDateTime.MIN)
                            ? "-" : score.date().format(dateFormat));

                    setText(null);
                    setGraphic(cellBox);
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

    public void onViewAllScoresClicked(ActionEvent actionEvent) {
        ViewSwitcher.switchTo("leaderboard.fxml");
    }
}