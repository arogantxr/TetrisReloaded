

package htl.steyr.tetris;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;



public class TetrisGame {

    private AnchorPane root;
    private Tile tile = new Tile();
    private Tile spare = new Tile();
    private Tile hold = new Tile();
    private int scale;
    private boolean holdBool = false;
    private String keyString;

    public TetrisGame(AnchorPane root){
        this.root = root;

        root.getChildren().add(tile);
        scale = (int) tile.getScale();

        root.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                root.setFocusTraversable(true);
                Platform.runLater(() -> root.requestFocus());
                root.setOnKeyPressed(event -> {
                    keyString = event.getCode().toString();
                    if(keyString == "I"){

                    }
                    //System.out.println(keyString);
                    move(keyString);
                });
            }
        });
    }

    public void update(double dt){
        // tile.moveHorizon(-1);
    }

    public void move(String keybind){
        switch (keybind){
            case "LEFT": {tile.moveVertic(scale); break;}
            case "RIGHT": {tile.moveVertic(- scale); break;}
            case "DOWN": {tile.moveHorizon(- scale); break;}
            case "UP": {tile.rotato(90); break;}
            case "X": {tile.rotato(- 90); break;}
            case "Y": {
                if(!holdBool){
                    hold = tile;
                    // new random tile spawn here
                    holdBool = true;
                    break;
                }
                spare = tile;
                tile = hold;
                hold = spare;
                break;
            }
            case "SPACE": {
                // the moveDown function (collision check needed)
                break;
            }

        }
    }

    public void getKey(String key){
        this.keyString = key;
    }


}
