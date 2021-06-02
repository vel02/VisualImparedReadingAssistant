package sti.software.engineering.reading.assistant.util;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Objects;

public class Utility {


    public static class Permissions {

        private Permissions() {
        }

        public static final int CAMERA_REQUEST_CODE = 101;
        public static final int STORAGE_REQUEST_CODE = 201;

    }

    public static class Files {

        public static final String FILE_EXTENSION_JPG = ".jpg";
        public static final String FILE_PATH_SEPARATOR_SLASH = "/";
        public static final String FILE_PATH_DOT = ".";

        private Files() {
        }

        public static boolean renameFile(File from, File to) {
            return Objects.requireNonNull(from.getParentFile()).exists() && from.exists() && from.renameTo(to);
        }

        public static Uri getUriForFile(Context context, File file) {
            return FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName()
                            + ".provider", file);
        }
    }

    public static class Messages {
        private Messages() {
        }

        public static void toastMessage(Context context, String message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }

        public static void toastMessage(Context context, String message, int length) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

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

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }
}
