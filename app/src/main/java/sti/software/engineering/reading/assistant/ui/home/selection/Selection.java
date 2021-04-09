package sti.software.engineering.reading.assistant.ui.home.selection;

import android.content.Intent;
import android.net.Uri;

@Deprecated
public abstract class Selection {

    abstract Intent pickCamera();

    abstract Intent pickGallery();

    abstract Uri getImageUri();
}
