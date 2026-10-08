package htl.steyr.tetris;

import javafx.animation.AnimationTimer;

import java.sql.SQLOutput;


public class GameLoop extends AnimationTimer {

    private final TetrisGame tetrisGame;

     // simulation ticks

    private int frames = 0;

    private long usedMemory;
    private long lastTime = 0;
    private long lastFpsTime = 0;

    private double fps = 0;
    private double speed = 3.0; // 1x speed
    private double accu;
    private double step = 1.0 / 60.0;

    private boolean running = true;
    private boolean framerateSwitch = false;


    public GameLoop(TetrisGame tetrisGame) {
        this.tetrisGame = tetrisGame;

    }

    public void pause() {
        running = false;
    }

    public void resume() {
        lastTime = 0;
        running = true;
    }


    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed(){
        return this.speed;
    }

    @Override
    public void handle(long now) {
        if (running) {
            if (lastTime == 0) {
                lastTime = now;
                lastFpsTime = now;
                return;
            }

            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            delta = Math.min(delta, 0.1);
            accu += delta * speed;

            while(accu >= step){
                tetrisGame.update(step);
                accu -= step;
            }

            frames++;
            // this.updateFPS(now);

        }
    }

    private void updateFPS(double now){
        // Framerate display in console

        if (now - lastFpsTime >= 500_000_000L) {

            long total = Runtime.getRuntime().totalMemory();
            long free  = Runtime.getRuntime().freeMemory();
            long used  = total - free;

            long usedMB  = used / 1_000_000;
            fps = frames;
            frames = 0;

            System.out.println("fps: " + fps + " | Memory:  " + usedMB + " mb");
        }
    }
}
