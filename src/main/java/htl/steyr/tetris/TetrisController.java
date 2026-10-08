package htl.steyr.tetris;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TetrisController {

    @FXML
    private AnchorPane root;
    private TetrisGame tetrisGame;
    private GameLoop loop;

    public void initialize(){
        tetrisGame = new TetrisGame(root);

        loop = new GameLoop(tetrisGame);

        loop.start();



        loop = new GameLoop(tetrisGame) {
            @Override
            public void handle(long now){
                super.handle(now);
            }
        };

        //loop.start(); // Toast mit Schinken
    }
}
