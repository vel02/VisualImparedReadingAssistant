package sti.software.engineering.reading.assistant.ui.home.selection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import sti.software.engineering.reading.assistant.ui.home.selection.factory.ComponentFactory;

public class SelectImageFrom {

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

}
