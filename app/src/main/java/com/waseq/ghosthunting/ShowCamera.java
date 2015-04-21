package com.waseq.ghosthunting;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holdMe;
    private Camera theCamera;

    public ShowCamera(Context context, Camera camera) {
        super(context);
        theCamera = camera;
        holdMe = getHolder();
        holdMe.addCallback(this);
        holdMe.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // 2.33 compatibility


    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {


        if (holdMe.getSurface() == null) {
            // preview surface does not exist
            return;
        }


        try {
            theCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            theCamera.setPreviewDisplay(holdMe);
            theCamera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        return;
        try {
            if (theCamera != null) {
                theCamera.setPreviewDisplay(holder);
                theCamera.startPreview();
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        if (theCamera != null) {
            theCamera.stopPreview();
            theCamera.release();
        }
    }

    public float getCameraAngle() {
        if (theCamera != null)
            return theCamera.getParameters().getHorizontalViewAngle();    // todo check
        return 50;
    }

    public float getHorizontalCameraAngle() {
        if (theCamera != null)
            return theCamera.getParameters().getHorizontalViewAngle();    // todo check
        return 50;
    }

    public float getVerticalCameraAngle() {
        if (theCamera != null)
            return theCamera.getParameters().getVerticalViewAngle();    // todo check
        return 50;
    }

}