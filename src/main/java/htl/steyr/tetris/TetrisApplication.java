package htl.steyr.tetris;

import htl.steyr.tetris.utility.ViewSwitcher;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class TetrisApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Tetris");
        Image image = new Image(TetrisApplication.class.getResource("/image/logo_tetris.png").toExternalForm());
        stage.getIcons().add(image);
        stage.setResizable(true);
        ViewSwitcher.setStage(stage);
        ViewSwitcher.switchTo("start.fxml");
    }
}
