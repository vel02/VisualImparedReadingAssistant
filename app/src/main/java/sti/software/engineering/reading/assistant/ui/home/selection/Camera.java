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
    private static final String SEPARATOR = File.separator;
    private static final String FOLDER_NAME = "VisualImpairedImages";

    private final Context context;

    private Uri imageUri;
    private File fromFile;
    private String filename;

    public Camera(Context context) {
        this.context = context;
    }


    @Override
    public Intent selectImage() {

        String external_storage_directory = Environment.getExternalStorageDirectory().toString();

        File folder = new File(external_storage_directory + SEPARATOR + "Pictures"
                + SEPARATOR + FOLDER_NAME);
        if (!folder.exists()) {
            if (folder.mkdir()) Log.d(TAG, "STATUS SUCCESS: FOLDER CREATED SUCCESSFULLY!");
        } else Log.w(TAG, "STATUS FAILED: FOLDER EXISTS IN PHONE DIRECTORY.");

        this.filename = this.createImageFileName();
        this.fromFile = new File(external_storage_directory + SEPARATOR + "Pictures"
                + SEPARATOR + FOLDER_NAME + SEPARATOR + filename);
        this.imageUri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName()
                        + ".provider", this.fromFile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        return cameraIntent;
    }

    private String createImageFileName() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        return "image_" + (System.currentTimeMillis() / 1000)
                + "_" + month + "" + day + "" + year + ".jpg";
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }

    @Override
    public File getFile() {
        return this.fromFile;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
