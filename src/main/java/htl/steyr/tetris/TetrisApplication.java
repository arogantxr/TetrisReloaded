package htl.steyr.tetris;

import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class TetrisApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Tetris");
        stage.setResizable(false);
        ViewSwitcher.setStage(stage);
        ViewSwitcher.switchTo("start.fxml");
    }
}
