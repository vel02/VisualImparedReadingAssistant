package sti.software.engineering.reading.assistant;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import sti.software.engineering.reading.assistant.di.DaggerAppComponent;

public class BaseApplication extends DaggerApplication {

    public static final String NOTIFICATION_CHANNEL_ID = "sti.software.engineering.reading.assistant.CHANNEL_TRIGGER_CAMERA";
    public static final String NOTIFICATION_CHANNEL_NAME = "Open Camera";

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Directly open the application camera using power button.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }
}
