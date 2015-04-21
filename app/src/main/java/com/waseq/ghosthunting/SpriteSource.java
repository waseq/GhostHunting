package com.waseq.ghosthunting;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Created by waseq on 14.1.15.
 */
public class SpriteSource {
    int animationLen;
    Bitmap[] animation;

    public static Sprite getNewSprite(TYPE type) {
        return new Sprite(get(type));
    }

    public static TYPE randomType(Random rnd) {
        int n = rnd.nextInt(5);
        switch (n) {
            case 0:
                return TYPE.GH1;
            case 1:
                return TYPE.GH2;
            default:
                return TYPE.GH3;
        }
    }

    public static enum TYPE {GH1, GH2, GH3}

    TYPE type;

    private SpriteSource(TYPE type) {
        this.type = type;
        animation = new Bitmap[10];
        switch (type) {
            case GH1:
                animation[0] = BitmapFactory.decodeResource(res, R.drawable.duch1_1);
                animation[1] = BitmapFactory.decodeResource(res, R.drawable.duch1_2);
                animation[2] = BitmapFactory.decodeResource(res, R.drawable.duch1_3);
                animation[3] = BitmapFactory.decodeResource(res, R.drawable.duch1_4);
                animation[4] = BitmapFactory.decodeResource(res, R.drawable.duch1_5);
                animationLen = 4;
                break;
            case GH2:
                animation[0] = BitmapFactory.decodeResource(res, R.drawable.duch2_1);
                animation[1] = BitmapFactory.decodeResource(res, R.drawable.duch2_2);
                animation[2] = BitmapFactory.decodeResource(res, R.drawable.duch2_3);
                animation[3] = BitmapFactory.decodeResource(res, R.drawable.duch2_4);
                animation[4] = BitmapFactory.decodeResource(res, R.drawable.duch2_5);
                animation[5] = BitmapFactory.decodeResource(res, R.drawable.duch2_6);
                animation[6] = BitmapFactory.decodeResource(res, R.drawable.duch2_7);
                animation[7] = BitmapFactory.decodeResource(res, R.drawable.duch2_8);
                animation[8] = BitmapFactory.decodeResource(res, R.drawable.duch2_9);
                animation[9] = BitmapFactory.decodeResource(res, R.drawable.duch2_10);
                animationLen = 9;
                break;
            case GH3:
                animation[0] = BitmapFactory.decodeResource(res, R.drawable.duch3_1);
                animation[1] = BitmapFactory.decodeResource(res, R.drawable.duch3_2);
                animation[2] = BitmapFactory.decodeResource(res, R.drawable.duch3_3);
                animation[3] = BitmapFactory.decodeResource(res, R.drawable.duch3_4);
                animation[4] = BitmapFactory.decodeResource(res, R.drawable.duch3_5);
                animation[5] = BitmapFactory.decodeResource(res, R.drawable.duch3_6);
                animation[6] = BitmapFactory.decodeResource(res, R.drawable.duch3_7);
                animation[7] = BitmapFactory.decodeResource(res, R.drawable.duch3_8);
                animationLen = 7;
                break;
        }


    }

    //static ArrayList<SpriteSource> sources;
    static SpriteSource gh1 = null;
    static SpriteSource gh2 = null;
    static SpriteSource gh3 = null;

    public static Resources res;

    private static SpriteSource get(TYPE type) {
        switch (type) {
            case GH1:
                if (gh1 == null) {
                    gh1 = new SpriteSource(TYPE.GH1);
                }
                return gh1;
            case GH2:
                if (gh2 == null) {
                    gh2 = new SpriteSource(TYPE.GH2);
                }
                return gh2;
            case GH3:
                if (gh3 == null) {
                    gh3 = new SpriteSource(TYPE.GH3);
                }
                return gh3;
        }
        return null;
    }

    public Bitmap getBmp(int pos) {
        return animation[pos];
    }

    public int getAnimationLength() {
        return animationLen;
    }
}
