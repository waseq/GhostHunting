package com.waseq.ghosthunting;


import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by waseq on 21.12.14.
 */
public class Engine {
    ArrayList<Ghost> ghosts;
    private Timer timer;
    private boolean running = false;
    private double azimuth;
    private double roll;
    private double pitch;
    private long score;
    private Timer t;
    private Random rnd;
    private int ghostsKilled = 0;

    public int getGhostsKilled() {
        return ghostsKilled;
    }

    public long getScore() {
        return score;
    }

    public int getLevel() {
        return ghostsKilled / 10 + 1;
    }

    public void addGhost(Ghost ghost) {
        ghosts.add(ghost);
    }

    Engine() {

        ghosts = new ArrayList<Ghost>();
        ghosts.add(new Ghost(new GameLocation(10, -90, 100), SpriteSource.TYPE.GH1));
        ghosts.add(new Ghost(new GameLocation(20, -80, 300), SpriteSource.TYPE.GH2));
        ghosts.add(new Ghost(new GameLocation(180, -80, 400), SpriteSource.TYPE.GH3));
        ghosts.add(new Ghost(new GameLocation(-160, -90, 500), SpriteSource.TYPE.GH1));

        t = new Timer();
        rnd = new Random();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                longUpdate();

            }
        }, 10000, 5000);

    }

    public synchronized ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * herni update.
     */
    protected synchronized void update() {
        ArrayList<Ghost> gh2 = new ArrayList<Ghost>();
        for (Ghost g : ghosts) {
            if (g.getState() != GameObject.State.dead) {
                gh2.add(g);
            } else {
                notifyGhostKilled();
            }
        }
        ghosts = gh2;
        for (Ghost g : ghosts) {
            g.move();
        }
    }

    protected synchronized void longUpdate() {
        int level = (ghostsKilled / 10) + 1;
        int maxGhosts = Math.min(level * 5, 30);
        if (ghosts.size() < maxGhosts) {
            int generate = level;
            for (int i = 0; i < generate; i++) {
                addGhost(new Ghost(GameLocation.randomGoodLocation(rnd), SpriteSource.randomType(rnd)));
            }
        }
    }

    private void notifyGhostKilled() {
        score += 10;
        ghostsKilled++;
    }

    public void start() {
        if (timer == null) {
            timer = new Timer();
        }

        if (!running) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, 250, 250);
            running = true;
        }
    }

    /**
     * Fire button pressed.
     */
    public void fire() {
        for (Ghost g : ghosts) {
            if (g.getLocation().inRadius(azimuth, pitch, roll, 10)) {
                g.damage(10);
                System.out.println("hit");
                break;
            }
        }
    }

    public void rotationChange(double azimuth, double pitch, double roll) {
        this.azimuth = azimuth;
        this.pitch = pitch;
        this.roll = roll;
    }
}
