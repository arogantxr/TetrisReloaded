package htl.steyr.tetris.key;

import javafx.scene.input.KeyCode;
import java.io.Serializable;
import java.security.Key;

public class KeyAssignment implements Serializable {
    private KeyCode moveLeft = KeyCode.LEFT;
    private KeyCode moveRight = KeyCode.RIGHT;
    private KeyCode moveDown = KeyCode.DOWN;
    private KeyCode rotateRight = KeyCode.R;
    private KeyCode rotateLeft = KeyCode.Z;
    private KeyCode softDrop = KeyCode.S;
    private KeyCode hardDrop = KeyCode.SPACE;
    private KeyCode hold = KeyCode.Y;

    public KeyCode getMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(KeyCode moveLeft) {
        this.moveLeft = moveLeft;
    }

    public KeyCode getMoveRight() {
        return moveRight;
    }

    public void setMoveRight(KeyCode moveRight) {
        this.moveRight = moveRight;
    }

    public KeyCode getRotateRight() {
        return rotateRight;
    }

    public void setRotateRight(KeyCode rotateRight) {
        this.rotateRight = rotateRight;
    }

    public KeyCode getSoftDrop() {
        return softDrop;
    }

    public void setSoftDrop(KeyCode softDrop) {
        this.softDrop = softDrop;
    }

    public KeyCode getHardDrop() {
        return hardDrop;
    }

    public void setHardDrop(KeyCode hardDrop) {
        this.hardDrop = hardDrop;
    }

    public KeyCode getHold() {
        return hold;
    }

    public void setHold(KeyCode hold) {
        this.hold = hold;
    }

    public KeyCode getRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(KeyCode rotateLeft) {
        this.rotateLeft = rotateLeft;
    }

    public KeyCode getMoveDown() {
        return moveDown;
    }

    public void setMoveDown(KeyCode moveDown) {
        this.moveDown = moveDown;
    }
}