package sti.software.engineering.reading.assistant.util;

import android.app.ActivityManager;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

public class Utility {


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void refreshGallery(Context context, String filename) {
        final String FOLDER_NAME = "VisualImpairedImages";
        MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStorageDirectory()
                        .toString() + "/Pictures/" + FOLDER_NAME + "/" + filename}, null,
                (path, uri) -> Log.i("home", "Scanned " + path));
    }

}
