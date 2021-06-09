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
import android.speech.tts.TextToSpeech;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import sti.software.engineering.reading.assistant.BaseApplication;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.receiver.DetectPowerClickedReceiver;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;
import sti.software.engineering.reading.assistant.util.TextToSpeechHelper;

public class TriggerCameraService extends Service implements DetectPowerClickedReceiver.OnScreenReceiverCallback {

    public static final String INTENT_STARTED_THROUGH_SERVICE = "started_via_service";
    private static final int NOTIFICATION_TRIGGER_CAMERA_ID = 1;

    private boolean isChangeConfiguration;

    private DetectPowerClickedReceiver screenOnReceiver;
    private TextToSpeechHelper textToSpeech;

    @Override
    public void onTriggered() {
        if (serviceIsRunningInForeground(TriggerCameraService.this, TriggerCameraService.this)) {
            textToSpeech.speak("Camera, Activated.", TextToSpeech.QUEUE_FLUSH);
            Intent intent = new Intent(TriggerCameraService.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        textToSpeech = new TextToSpeechHelper(this);//TextToSpeechHelper.getInstance(this);
        screenOnReceiver = new DetectPowerClickedReceiver();
        screenOnReceiver.onScreenReceiverCallback(this);
        registerScreenOnReceiver();

        return START_STICKY;
    }

    private void registerScreenOnReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
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
        if (textToSpeech != null) textToSpeech.destroy();
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
