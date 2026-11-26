package htl.steyr.tetris.controller;

import htl.steyr.tetris.highscore_management.HighscoreManager;
import htl.steyr.tetris.highscore_management.Score;
import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeaderboardController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    private ListView<Score> leaderboardList;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        leaderboardList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Score score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                } else {
                    String date = score.date().equals(LocalDateTime.MIN)
                            ? "-" : score.date().format(DATE_FORMAT);
                    setText(String.format("%02d    %-18s %,d Punkte    Level %-3d Lines %-4d %s",
                            getIndex() + 1, score.name(), score.value(), score.level(), score.lines(), date));
                }
            }
        });
        leaderboardList.getItems().setAll(HighscoreManager.loadHighscores());
    }

    @FXML
    private void onBackButtonClicked(ActionEvent actionEvent) {
        ViewSwitcher.switchTo("menu.fxml");
    }
}
