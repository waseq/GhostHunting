package com.waseq.ghosthunting;

/**
 * Created by waseq on 6.12.14.
 */
public class Ghost extends GameObject {
    Ghost(GameLocation location, SpriteSource.TYPE type) {
        super(location, GameLocation.LOCATION_PLAYER, SpriteSource.getNewSprite(type));
        this.hp = 50;

    }

}
