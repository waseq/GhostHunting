package com.waseq.ghosthunting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;

/**
 * Created by waseq on 6.12.14.
 */
public class RadarView extends View {
    private boolean active = false;
    private int transparency;
    private double azimuth = 0;
    private Engine engine;

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public RadarView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.draw(canvas);
        if (active) {
            Paint m = new Paint();

            m.setARGB(transparency, 10, 10, 40);
            m.setStyle(Paint.Style.FILL);
            canvas.drawRect(new Rect(0, 0, this.getWidth(), this.getHeight()), m);

            // kreslime jen pri uplnem zatemneni
            if (transparency != 255) return;

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

            canvas.drawBitmap(bmp, getWidth() / 2 - bmp.getWidth() / 2, getHeight() / 2 - bmp.getHeight() / 2, null);

            PointF center = new PointF(getWidth() / 2, getHeight() / 2);
            Paint p = new Paint();
            p.setColor(Color.RED);

            // kreslime duchy
            if (engine != null) {
                for (Ghost g : engine.getGhosts()) {
                    // draw ghost dot
                    PointF rp = g.location.getRadarPoint(center, azimuth);
                    //System.out.println(rp.x+" "+rp.y);
                    // uprava pro male TODO better
                    if (rp.x <= 0) rp.x = 0;
                    if (rp.y <= 0) rp.y = 0;
                    if (rp.x > getWidth()) rp.x = getWidth();
                    if (rp.y > getHeight()) rp.y = getHeight();

                    canvas.drawCircle(rp.x - 5, rp.y - 5, 10, p);
                }
            }

            // kreslime vyhled
            // TODO


        }

    }

    void activate() {
        active = true;
        Button b = (Button) findViewById(R.id.butt);
        if (b != null)
            b.setVisibility(View.INVISIBLE);

        invalidate();
    }

    void disable() {
        active = false;
        invalidate();
        Button b = (Button) findViewById(R.id.butt);
        if (b != null)
            b.setVisibility(View.VISIBLE);

    }

    public void rotationChange(double azimuth, double pitch, double roll) {
        if ((roll <= 35) && (roll >= -35)) {
            activate();
            transparency = (int) (Math.abs(roll) > 20.0 ? (255.0 - ((Math.abs(roll) - 20) * 255.0) / 10.0) : 255);
            this.azimuth = azimuth;
            //System.out.println("Active "+transparency);
        } else {
            disable();

        }
    }
}
