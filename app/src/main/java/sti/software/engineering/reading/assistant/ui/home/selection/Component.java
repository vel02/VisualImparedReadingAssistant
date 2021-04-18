package sti.software.engineering.reading.assistant.ui.home.selection;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

public interface Component {

    Intent selectImage();

    Uri getImageUri();

    File getFile();

}
