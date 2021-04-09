package sti.software.engineering.reading.assistant.ui.home.selection;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Gallery implements Component {

    private final Context context;
    private Uri imageUri;


    public Gallery(Context context) {
        this.context = context;
    }

    @Override
    public Intent selectImage() {
        return null;
    }

    @Override
    public Uri getImageUri() {
        return imageUri;
    }
}
