package sti.software.engineering.reading.assistant.ui.home.selection;


import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class Gallery implements Component {

    @Override
    public Intent selectImage() {
        return new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
    }

    @Override
    public Uri getImageUri() {
        return null;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public String getFilename() {
        return "";
    }
}
