package com.firefly.faceEngine.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.firefly.faceEngine.utils.Preferences;


/**
 * Created by firefly on 2017/3/13.
 */
public class GraySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mSurfaceHolder;

    public GraySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        GrayInterface.getInstance().openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        GrayInterface.getInstance().startPreview(surfaceHolder, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        GrayInterface.getInstance().stopCamera();
    }

    public int getCameraId(){
        return GrayInterface.getInstance().getCameraId();
    }

    public SurfaceHolder getSurfaceHolder(){
        return mSurfaceHolder;
    }
}
