package com.test.app.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ptst.mylibrary.SubMainActivity;

public class MainActivity extends Activity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST = 10;
    private ImageView imagePreview;
    private TextView txtStatCam;

    private static File getAppFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "APP_TEST");
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_TEST_APP.jpg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagePreview = (ImageView) findViewById(R.id.image_preview);
        txtStatCam = (TextView) findViewById(R.id.text_cam_status);
        if (!isDeviceSupportCamera()) {
            txtStatCam.setText("Device Does not support Camera");
        }
    }

    public void onStartLib(View view) {
        Intent intent = new Intent(this, SubMainActivity.class);
        startActivity(intent);
    }

    public void onStartCamera(View view) {
        startCamera();
    }

    private void startCamera() {
        if (isDeviceSupportCamera()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isCameraPermitted()) {
                txtStatCam.setText("Device Camera Allowed to use");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getAppFile());
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST);
            } else {
                txtStatCam.setText("Device Camera not not allowed to use");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                txtStatCam.setText("Camera Result OK");
                imagePreview.setVisibility(View.VISIBLE);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(getAppFile().getPath(), options);
                    imagePreview.setImageBitmap(bitmap);
                } catch (NullPointerException e) {
                    txtStatCam.setText("Camera Result OK - Null");
                    imagePreview.setVisibility(View.GONE);
                }
            } else if (resultCode == RESULT_CANCELED) {
                txtStatCam.setText("Camera Result CANCELED");
                imagePreview.setVisibility(View.GONE);
            } else {
                txtStatCam.setText("Camera Result Unknown");
                imagePreview.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                txtStatCam.setText("Device Camera not not allowed to use");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isCameraPermitted() {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
