package sti.software.engineering.reading.assistant.ui.home.selection.factory;

import android.content.Context;

import sti.software.engineering.reading.assistant.ui.home.selection.Camera;
import sti.software.engineering.reading.assistant.ui.home.selection.Component;
import sti.software.engineering.reading.assistant.ui.home.selection.Gallery;

public class ComponentFactory {

    public static final int SELECT_CAMERA = 101;
    public static final int SELECT_GALLERY = 201;

    private final Context context;

    public ComponentFactory(Context context) {
        this.context = context;
    }

    public Component selectImageFrom(int from) {
        if (from == SELECT_CAMERA) {
            return new Camera(context);
        } else if (from == SELECT_GALLERY) {
            return new Gallery();
        }
        return new Gallery();
    }

}
