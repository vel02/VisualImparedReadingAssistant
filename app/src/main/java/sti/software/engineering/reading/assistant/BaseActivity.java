package sti.software.engineering.reading.assistant;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import dagger.android.support.DaggerAppCompatActivity;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;

public class BaseActivity extends DaggerAppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int STORAGE_REQUEST_CODE = 201;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 301;

    public static final int IMAGE_PICK_CAMERA_CODE = 1001;
    public static final int IMAGE_PICK_AUTO_CAMERA_CODE = 1002;
    public static final int IMAGE_PICK_GALLERY_CODE = 2001;

    protected TriggerCameraService triggerCameraService;
    protected boolean isServiceBound;

    protected final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TriggerCameraService.TriggerCameraBinder binder = (TriggerCameraService.TriggerCameraBinder) service;
            BaseActivity.this.triggerCameraService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BaseActivity.this.triggerCameraService = null;
            isServiceBound = false;
        }
    };

}
