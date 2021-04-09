package sti.software.engineering.reading.assistant.ui.home.selection;

import android.content.Intent;
import android.net.Uri;

public class SelectImageFrom {

    private final Component component;

    public SelectImageFrom(Component component) {
        this.component = component;
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

}
