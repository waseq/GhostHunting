package com.waseq.ghosthunting;


import android.graphics.Bitmap;

/**
 * Created by waseq on 6.12.14.
 */
public class GameObject {
    public State getState() {
        return state;
    }

    public static enum State {living, hit, dying, dead}

    ;
    protected State state = State.living;
    protected GameLocation location;
    protected GameLocation target;
    protected int hp;
    protected boolean wasHit = false;
    protected Sprite sprite;


    GameObject(GameLocation location) {
        this.location = location;
    }

    GameObject(GameLocation location, GameLocation target, Sprite sprite) {
        this.location = location;
        this.target = target;
        this.sprite = sprite;
    }

    public GameLocation getLocation() {
        return location;
    }

    /* pohyb k cili TODO */
    public void move() {
        if (location.getDistance() >= 50)
            location.distance -= 1;
    }

    public void damage(int dam) {
        this.hp -= dam;
        this.wasHit = true;
        this.state = State.hit;
        sprite.animateHit();
        if (this.hp <= 0) this.state = State.dead;
    }

    public Bitmap getBitmap() {
        return sprite.getBmp();
    }
}
