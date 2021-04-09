package sti.software.engineering.reading.assistant.ui.home.selection;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class Camera implements Component {

    private final Context context;
    private Uri imageUri;

    public Camera(Context context) {
        this.context = context;
    }

    @Override
    public Intent selectImage() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "image_capture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");

        this.imageUri = context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        return cameraIntent;
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }
}
