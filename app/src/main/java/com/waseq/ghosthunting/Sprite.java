package com.waseq.ghosthunting;

import android.graphics.Bitmap;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by waseq on 14.1.15.
 */
public class Sprite {
    SpriteSource spriteSource;
    Timer t = new Timer();
    int pos = 0;
    int max;

    Sprite(SpriteSource spriteSource) {
        this.spriteSource = spriteSource;
        max = spriteSource.getAnimationLength();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                nextFrame();
            }
        }, 250, 500);
    }

    public Bitmap getBmp() {
        return spriteSource.getBmp(pos);
    }

    protected void nextFrame() {
        pos = (pos + 1) % max;
    }

    public void animateHit() {
        this.pos = spriteSource.getAnimationLength();
    }
}
