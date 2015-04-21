package com.waseq.ghosthunting;

import android.graphics.PointF;

import java.util.Random;

/**
 * Basic class that represents relative location to the device.
 * Created by waseq on 21.12.14.
 */
public class GameLocation {
    /**
     * The azimuth on which is the object visible.
     */
    public static double MAX_DISTANCE = 600;
    public static GameLocation LOCATION_PLAYER = new GameLocation(0, 0, 0);

    protected double azimuth;
    /**
     * The roll under which the object is visible.
     */
    protected double roll;
    /**
     * The distance from player.
     */
    protected double distance;

    GameLocation(double azimuth, double roll, double distance) {
        this.azimuth = azimuth;
        this.roll = roll;
        this.distance = distance;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getDistance() {
        return distance;
    }

    public double getRoll() {
        return roll;
    }

    /**
     * Vraci uhlel mezi azimutem a a b.
     *
     * @param a
     * @param b
     * @return uhel
     */
    public static double getAzimuthAngle(double a, double b) {

        return (b - a) % 360;


    }

    double getScreenX(double cameraAngle, double deviceAzimuth, double screenWidth) {
        double azimuthL = deviceAzimuth - (cameraAngle / 2);  // azimuth of left corner
        double azimuthR = deviceAzimuth + (cameraAngle / 2);
        double azfix = azimuth;
        if (azimuthR >= 180 && azimuth < 0) azfix = 360 + azimuth;
        if (azimuthL <= -180 && azimuth > 0) azfix = -360 + azimuth;
        return ((azfix - azimuthL) / cameraAngle * screenWidth);
    }

    double getScreenY(double cameraAngle, double deviceRoll, double screenHeight) {

        double rollB = deviceRoll - (cameraAngle / 2);
        double rollT = deviceRoll + (cameraAngle / 2);

        return ((roll - rollB) / cameraAngle * screenHeight);
    }

    public PointF getRadarPoint(PointF center, double deviceAzimuth) {
        double dx = Math.cos(Math.toRadians(azimuth - deviceAzimuth - 90)) * distance;
        double dy = Math.sin(Math.toRadians(azimuth - deviceAzimuth - 90)) * distance;
        return new PointF((float) (center.x + dx), (float) (center.y + dy));
    }

    public double getScaleFactor() {

        return Math.max((MAX_DISTANCE - distance) / MAX_DISTANCE, 0.1);
    }

    /**
     * Zda je tato location viditelna. Ctverec.
     *
     * @param azimuth
     * @param pitch   se nepouziva
     * @param roll
     * @param radius
     * @return true iff viditelne
     */
    public boolean inRadius(double azimuth, double pitch, double roll, double radius) {

        if (Math.abs(roll - this.roll) <= radius) {
            if (Math.min(Math.abs(getAzimuthAngle(azimuth, this.azimuth)), Math.abs(getAzimuthAngle(this.azimuth, azimuth))) <= radius) {

                return true;
            }
        }

        return false;
    }

    public static GameLocation randomGoodLocation(Random rnd) {
        return new GameLocation(rnd.nextDouble() * 358.0 - 179.0, -90.0 + rnd.nextGaussian() * 20, rnd.nextDouble() * 100 + MAX_DISTANCE - 100);
    }
}
