package sti.software.engineering.reading.assistant;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import dagger.android.support.DaggerAppCompatActivity;

public class BaseActivity extends DaggerAppCompatActivity {

    protected static final int CAMERA_REQUEST_CODE = 101;

    protected static final int IMAGE_PICK_CAMERA_CODE = 1001;

    protected void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CAMERA_REQUEST_CODE);
    }

    protected boolean checkCameraPermission() {
        boolean cameraPermitted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermitted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return cameraPermitted && storagePermitted;
    }

}
