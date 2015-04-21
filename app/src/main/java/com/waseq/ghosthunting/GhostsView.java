package com.waseq.ghosthunting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by waseq on 24.11.14.
 */
public class GhostsView extends View {

    private final float horizontalCameraAngle;
    private final float verticalCameraAngle;

    private Engine engine;
    private double azimuth;
    private double roll;
    private boolean enabled = true;


    public GhostsView(Context context, float vcamangle, float hcamangle) {
        super(context);
        SpriteSource.res = getResources();
        this.horizontalCameraAngle = hcamangle;
        this.verticalCameraAngle = vcamangle;
        System.out.println("H: " + horizontalCameraAngle + " V: " + verticalCameraAngle);
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!enabled) return;
        //super.draw(canvas);
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        for (Ghost g : engine.getGhosts()) {
            Bitmap bmp = g.getBitmap();

            //canvas.drawBitmap(bmp, (float)g.getLocation().getScreenX(verticalCameraAngle,azimuth,this.getWidth()), (float) g.getLocation().getScreenY(horizontalCameraAngle,roll,this.getHeight()), null);
            float x = (float) g.getLocation().getScreenX(verticalCameraAngle, azimuth, this.getWidth());
            float y = (float) g.getLocation().getScreenY(horizontalCameraAngle, roll, this.getHeight());
            float w = (float) (g.getLocation().getScaleFactor() * bmp.getWidth());
            float h = (float) (g.getLocation().getScaleFactor() * bmp.getHeight());

            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), new RectF(x - w / 2, y - h / 2, x + w / 2, y + h / 2), null);

        }

        // draw crosshair

        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.crosshair);
        canvas.drawBitmap(bmp2, this.getWidth() / 2 - bmp2.getWidth() / 2, this.getHeight() / 2 - bmp2.getHeight(), null);


    }

    /**
     * A method that should be invoked after changing the rotation of the device
     *
     * @param
     * @param
     * @param
     */
    public void rotationChange(double azimuth, double pitch, double roll) {

        if ((roll > 35) || (roll < -35)) {
            activate();
        } else {
            disable();
        }
        this.azimuth = azimuth;

        this.roll = roll;

//        double azimuthL = azimuth - (cameraAngle /2);  // azimuth of left corner
//        double azimuthR = azimuth + (cameraAngle /2);
//        if((ghostAzimuth > azimuthL) && (ghostAzimuth<azimuthR)){
//           x = (float) ((ghostAzimuth - azimuthL)/ cameraAngle * this.getWidth());
//        }
//        else{
//            // out of bounds
//        }
//        double rollB = roll - (cameraAngle /2);
//        double rollT = roll + (cameraAngle /2);
//        if((ghostroll   > rollB) && (ghostroll<rollT)){
//            y = (float) ((ghostroll - rollB)/ cameraAngle * this.getHeight());
//        }
//        else{
//            // out of bounds
//        }


        this.invalidate();

    }

    private void disable() {
        enabled = false;
    }

    private void activate() {
        enabled = true;
    }
}

/*<div>Icon made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a></div>*/
