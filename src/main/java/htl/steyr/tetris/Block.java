package htl.steyr.tetris;

import javafx.scene.shape.Rectangle;

public class Block extends Rectangle {

    public Block(){
        setWidth(40);
        setHeight(40);

        setX(200);
        setY(100);
    }

    public void setScale(double scale){
        setWidth(getWidth() * scale);
        setHeight(getHeight() * scale);
    }

    public void setStartingPosition(int x, int y){
        setX(x);
        setY(y);
    }





}
