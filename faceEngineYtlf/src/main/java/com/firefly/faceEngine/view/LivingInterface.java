package com.firefly.faceEngine.view;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.firefly.arcterndemo.R;
import com.firefly.faceEngine.utils.Constants;
import com.firefly.faceEngine.utils.MatrixYuvUtils;
import com.firefly.faceEngine.utils.Tools;
import com.intellif.arctern.base.ArcternImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LivingInterface implements Camera.PreviewCallback {
    private Camera mCamera;
    private Camera.Parameters mParams;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Activity mActivity;
    private static LivingInterface mCameraInterface;
    private int mDisplayRotation;
    private int mDisplayOrientation;
    private int previewWidth = 640;
    private int previewHeight = 480;
    private LivingListener livingListener;

    private LivingInterface() {
    }

    public static synchronized LivingInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new LivingInterface();
        }
        return mCameraInterface;
    }

    public void init(Activity activity) {
        this.mActivity = activity;
    }

    public void setLivingCallBack(LivingListener listener) {
        this.livingListener = listener;
    }

    public void changeCamera(int cameraId, SurfaceHolder holder) throws IOException {
        mCamera.stopPreview();
        mCamera.release();
        mCamera.stopPreview();//停掉原来摄像头的预览
        mCamera.release();//释放资源
        mCamera = null;//取消原来摄像头
        mCamera = Camera.open(cameraId);//打开当前选中的摄像头
        try {
            mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.startPreview();//开始预览
    }

    private void saveToSDCard(String name, int resId) throws Throwable {
        InputStream inStream = mActivity.getApplication().getResources().openRawResource(resId);
        File file = new File(Environment.getExternalStorageDirectory(), name);
        FileOutputStream fileOutputStream = new FileOutputStream(file);//存入SDCard
        byte[] buffer = new byte[10];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] bs = outStream.toByteArray();
        fileOutputStream.write(bs);
        outStream.close();
        inStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void openCamera(int cameraId, SurfaceHolder surfaceHolder) {
        try {
            this.mCameraId = cameraId;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            mCamera = Camera.open(cameraId);
            Camera.getCameraInfo(cameraId, cameraInfo);
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            Tools.printStackTrace(e);
        }
    }

    public void startPreview(SurfaceHolder surfaceHolder, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();

            configureCamera(width, height);
            setDisplayOrientation();
            startPreview();
        } catch (Exception e) {
        }
    }

    // 停止预览，释放Camera
    public void stopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.setErrorCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    //获取Camera.Parameters
    public Camera.Parameters getCameraParams() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            return mParams;
        }
        return null;
    }

    public Camera getCameraDevice() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }

    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        // Set the PreviewSize and AutoFocus:..
        if (Tools.isLandscape()) {
            setOptimalPreviewSize2(parameters);
        } else {
            setOptimalPreviewSize(parameters);
        }
        setAutoFocus(parameters);
//		parameters.setZoom(10);
        // And set the parameters:
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setRotation(90);
        mCamera.setParameters(parameters);
    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters) {
        cameraParameters.setPreviewSize(previewWidth, previewHeight);
        cameraParameters.setPictureSize(previewWidth, previewHeight);
    }

    private void setOptimalPreviewSize2(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = getOptimalPreviewSize(sizes, Tools.getScreenWidth(), Tools.getScreenHeight());
        if (size != null) {
            Tools.debugLog("setOptimalPreviewSize2 size w=%s, h=%s", size.width, size.height);
            previewWidth = size.width;
            previewHeight = size.height;

            View parent = mActivity.findViewById(R.id.parent_preview);
            if (parent != null) {
                parent.getLayoutParams().width = previewWidth;
                parent.getLayoutParams().height = previewHeight;
            }
        }

        parameters.setPreviewSize(previewWidth, previewHeight);
        parameters.setPictureSize(previewWidth, previewHeight);

    }

    //设置聚焦
    private void setAutoFocus(Camera.Parameters cameraParameters) {
        List<String> supportedFocusModes = cameraParameters.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
    }

    private void setDisplayOrientation() {
        // Now set the display orientation:
        mDisplayRotation = MatrixYuvUtils.getDisplayRotation(mActivity);
        if(Constants.select_screen_rotate_rgbcamera == 0){
            mDisplayOrientation = MatrixYuvUtils.getDisplayOrientation(mDisplayRotation, mCameraId);
        }else{
            mDisplayOrientation = Constants.SCREEN_ROTATE[Constants.select_screen_rotate_rgbcamera];
        }
        mCamera.setDisplayOrientation(mDisplayOrientation);
    }

    private void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        ArcternImage arcternImage = new ArcternImage();
        arcternImage.gdata = data;
        arcternImage.width = previewHeight;
        arcternImage.height = previewWidth;
        arcternImage.image_format = ArcternImage.ARCTERN_IMAGE_FORMAT_NV21;
        arcternImage.frame_id = 0;
        livingListener.livingData(arcternImage);
    }

    public static void rotateYUV420Degree90(ArcternImage arcternImage){
        arcternImage.gdata = MatrixYuvUtils.rotateYUV420Degree90(arcternImage.gdata, arcternImage.height, arcternImage.width);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        float ASPECT_TOLERANCE = 0.1f;
        float targetRatio = h * 0.1f / w;

        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        float minDiff = Float.MAX_VALUE;

        for (Camera.Size size : sizes) {
            Tools.debugLog("size w=%s, h=%s", size.width, size.height);
            float ratio = size.width * 0.1f / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Float.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }

        return optimalSize;
    }

    private void kk(int width, int height){
        View parent = mActivity.findViewById(R.id.parent_preview);
        parent.getLayoutParams().width = width;
    }
}
