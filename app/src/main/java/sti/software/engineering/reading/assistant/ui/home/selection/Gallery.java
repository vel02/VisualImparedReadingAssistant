package sti.software.engineering.reading.assistant.ui.home.selection;


import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class Gallery implements Component {

    @Override
    public Intent selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }

    @Override
    public Uri getImageUri() {
        return null;
    }

    @Override
    public File getFile() {
        return null;
    }
}
