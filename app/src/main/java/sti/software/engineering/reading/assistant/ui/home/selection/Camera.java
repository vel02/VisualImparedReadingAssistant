package sti.software.engineering.reading.assistant.ui.home.selection;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.Calendar;

public class Camera implements Component {

    private static final String TAG = "Camera";
    private final Context context;
    private Uri imageUri;

    public Camera(Context context) {
        this.context = context;
    }

    @Override
    public Intent selectImage() {

        final String SEPARATOR = File.separator;
        final String FOLDER_NAME = "VisualImpairedStorage";
        String external_storage_directory = Environment.getExternalStorageDirectory().getAbsolutePath();

        File folder = new File(external_storage_directory + SEPARATOR + FOLDER_NAME);

        if (!folder.exists()) {
            if (folder.mkdir()) Log.d(TAG, "STATUS SUCCESS: FOLDER CREATED SUCCESSFULLY!");
        } else Log.w(TAG, "STATUS FAILED: FOLDER EXISTS IN PHONE DIRECTORY.");

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        File fromFile = new File(external_storage_directory + SEPARATOR + FOLDER_NAME
                + SEPARATOR + "image_" + (System.currentTimeMillis() / 1000)
                + "_" + month + "" + day + "" + year + ".png");

        this.imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", fromFile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        return cameraIntent;
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }
}
