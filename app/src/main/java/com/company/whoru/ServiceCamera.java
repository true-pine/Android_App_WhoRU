package com.company.whoru;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServiceCamera extends Service {

    final String TAG = "capturePhoto()";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.capturePhoto();
    }

    protected void capturePhoto() {
        Log.d(TAG, "사진촬영 준비 중");
        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        int frontCam = 1;

        Camera.getCameraInfo(frontCam, cameraInfo);

        try {
            camera = Camera.open(frontCam);
        } catch (RuntimeException e) {
            Log.d(TAG, "카메라 사용 불가");
            camera = null;
        }

        try {
            if(camera == null) {
                Log.d(TAG, "카메라 인스턴스를 얻을 수 없다");
            } else {
                Log.d(TAG, "카메라를 얻었고, 더미 서피스 텍스쳐 생성 중");

                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));

                    Camera.Parameters parameters = camera.getParameters();
                    List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                    Camera.Size pictureSize = pictureSizes.get(0);
                    parameters.setPictureSize(pictureSize.width, pictureSize.height);
                    camera.setParameters(parameters);

                    camera.startPreview();
                } catch (Exception e) {
                    Log.d(TAG, "이 서피스 프리뷰 텍스쳐로 설정할 수 없다");
                    e.printStackTrace();
                }

                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        saveImage(data);
                        camera.release();
                    }
                });
            }
        } catch (Exception e) {
            camera.release();
        }
        stopSelf();
    }

    protected void saveImage(byte[] data) {
        Bitmap finalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        finalBitmap = rotate(finalBitmap, 270);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/WHO.R.U");

        if(!myDir.exists()) {
            if(myDir.mkdirs()) {
                Log.d(TAG, "디렉토리 생성 완료");
            } else {
                Log.d(TAG, "디렉토리 생성 실패");
            }
        }

        String time = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        String fname = "Image" + time + ".jpg";
        File file = new File(myDir, fname);

        if(file.exists())
            file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Bitmap rotate(Bitmap bitmap, int degrees) {
        if(degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap = null;
                    bitmap = converted;
                    converted = null;
                }
            } catch (OutOfMemoryError ex) {
                Toast.makeText(getApplicationContext(), "메모리가 부족합니다", Toast.LENGTH_SHORT).show();
            }
        }
        return bitmap;
    }
}
