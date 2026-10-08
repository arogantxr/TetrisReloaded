package htl.steyr.tetris;


import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

import java.util.Random;


public class Tile extends Group {
    private Block base = new Block(); // basis spawn block, doesnt change
    private Block a = new Block();
    private Block b = new Block();
    private Block c = new Block();

    private int x;
    private int y;
    private double oldDreh = 0;
    private double scale = 1;
    private Rotate rotate = new Rotate();
    private boolean noRotate = false;
    private boolean halfRotate = false;

    private Point3D startpoint = new Point3D(10, 40, 0);

    public Tile(){

        createZ();
        this.getChildren().addAll(base, a, b, c);

        double centerX = base.getBoundsInLocal().getCenterX();
        double centerY = base.getBoundsInLocal().getCenterY();

        rotate.setPivotX(centerX);
        rotate.setPivotY(centerY);

        this.getTransforms().add(rotate);

        System.out.println(a.getBoundsInLocal());
        System.out.println(b.getBoundsInParent());
    }

    public void createT(){
        a.setX(a.getX() + (40 * scale));
        b.setY(b.getY() + (40 * scale));
        c.setX(c.getX() - (40 * scale));
    }

    public void createL(){
        a.setX(a.getX() + (40 * scale));
        b.setX(b.getX() - (40 * scale));
        c.setY(c.getY() + (40 * scale));
        c.setX(c.getX() + (40 * scale));
    }

    public void createRL(){
        a.setX(a.getX() + (40 * scale));
        b.setX(b.getX() - (40 * scale));
        c.setY(c.getY() + (40 * scale));
        c.setX(c.getX() - (40 * scale));
    }

    public void createI(){
        halfRotate = true;
        a.setX(a.getX() + (40 * scale));
        b.setX(b.getX() - (40 * scale));
        c.setY(c.getY() - (40 * scale));
    }

    public void createO(){
        noRotate = true;
        a.setX(a.getX() + (40 * scale));
        b.setY(b.getY() + (40 * scale));
        c.setX(c.getX() + (40 * scale));
        c.setY(c.getY() + (40 * scale));
    }

    public void createZ(){
        halfRotate = true;
        a.setX(a.getX() + (40 * scale));
        b.setY(b.getY() - (40 * scale));
        c.setX(c.getX() - (40 * scale));
        c.setY(c.getY() - (40 * scale));
    }

    public void createRZ(){
        halfRotate = true;
        a.setX(a.getX() + (40 * scale));
        b.setY(b.getY() + (40 * scale));
        c.setX(c.getX() + (40 * scale));
        c.setY(c.getY() - (40 * scale));
    }

    public void randomTile(String oldTile){
        Random rand = new Random();
        int random = rand.nextInt(7);

        switch (random) {
            case 0: {
                createT();
                break;
            }
            case 1: {
                createL();
                break;
            }
            case 2: {
                createRL();
                break;
            }
            case 3: {
                createI();
                break;
            }
            case 4: {
                createO();
                break;
            }
            case 5: {
                createZ();
                break;
            }
            case 6: {
                createRZ();
                break;
            }
        };
    }

    public void moveVertic(int n){
        this.setLayoutY(this.getLayoutY() - n);
    }

    public void moveHorizon(int n){
        this.setLayoutX(this.getLayoutX() - n);
    }

    public void move(int dx, int dy){
        setTranslateX(getTranslateX() + dx);
        setTranslateY(getTranslateY() + dy);
    }

    public double getScale(){
        System.out.println(base.getLayoutBounds().getWidth());
        return base.getLayoutBounds().getWidth();
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void rotato(double dreh){
        if(!noRotate){
            if(halfRotate && oldDreh != 0){
                rotate.setAngle(0);
                oldDreh = 0;
                return;
            }

            if(halfRotate && dreh < 0){
                rotate.setAngle(0 - dreh);
                oldDreh = dreh;
                return;
            }

            rotate.setAngle(oldDreh + dreh);
            oldDreh = oldDreh + dreh;
        }
    }

    public void hardDrop() {
        double step = 40 * scale;

        int safety = 0;

        while (!collidesBelow() && safety < 50) {
            this.setTranslateY(this.getTranslateY() + step);
            safety++;
        }
    }

    private boolean collidesBelow() {
        double blockSize = 40 * scale;

        double nextY = getTranslateY() + blockSize;

        return nextY >= 20 * blockSize;
    }
}
