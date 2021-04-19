package sti.software.engineering.reading.assistant.ui.home.selection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import sti.software.engineering.reading.assistant.ui.home.selection.factory.ComponentFactory;

public class SelectImageFrom {

    public static final int SELECT_CAMERA = 101;
    public static final int SELECT_GALLERY = 201;

    private final Component component;

    public SelectImageFrom(Context context, int from) {
        ComponentFactory factory = new ComponentFactory(context);
        this.component = factory.selectImageFrom(from);
    }

    public Intent pickCamera() {
        return this.component.selectImage();
    }

    public Intent pickGallery() {
        return this.component.selectImage();
    }

    public Uri getImageUri() {
        return this.component.getImageUri();
    }

    public File getFile() {
        return this.component.getFile();
    }

    public String getFilename() {
        return this.component.getFilename();
    }

}
