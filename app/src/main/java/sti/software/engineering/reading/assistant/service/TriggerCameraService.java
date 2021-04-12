package sti.software.engineering.reading.assistant.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import sti.software.engineering.reading.assistant.BaseApplication;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.receiver.DetectScreenOnReceiver;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;

public class TriggerCameraService extends Service implements DetectScreenOnReceiver.OnScreenReceiverCallback {


    public static final String INTENT_STARTED_THROUGH_SERVICE = "started_via_service";
    private static final int NOTIFICATION_TRIGGER_CAMERA_ID = 1;

    private boolean isChangeConfiguration;

    private DetectScreenOnReceiver screenOnReceiver;

    @Override
    public void onTriggered() {
        if (serviceIsRunningInForeground(TriggerCameraService.this, TriggerCameraService.this)) {
            Intent intent = new Intent(TriggerCameraService.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(INTENT_STARTED_THROUGH_SERVICE, true);
            startActivity(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.isChangeConfiguration = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        this.isChangeConfiguration = false;
        return new TriggerCameraBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        stopForeground(true);
        this.isChangeConfiguration = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!isChangeConfiguration) {
            startForeground(NOTIFICATION_TRIGGER_CAMERA_ID, notification());
        }
        return true;
    }

    private Notification notification() {
        return new NotificationCompat.Builder(this, BaseApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_app)
                .setContentTitle("Running...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setNotificationSilent()
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        screenOnReceiver = new DetectScreenOnReceiver();
        screenOnReceiver.onScreenReceiverCallback(this);
        registerScreenOnReceiver();

        return START_STICKY;
    }

    private void registerScreenOnReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnReceiver, filter);
    }

    private void unregisterScreenOnReceiver() {
        if (screenOnReceiver != null) {
            unregisterReceiver(screenOnReceiver);
            screenOnReceiver = null;
        }
    }

    public class TriggerCameraBinder extends Binder {
        public TriggerCameraService getService() {
            return TriggerCameraService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterScreenOnReceiver();
    }

    public static boolean serviceIsRunningInForeground(Context context, Object object) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (object.getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

}
