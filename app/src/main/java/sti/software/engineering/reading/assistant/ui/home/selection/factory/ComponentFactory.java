package sti.software.engineering.reading.assistant.ui.home.selection.factory;

import android.content.Context;

import sti.software.engineering.reading.assistant.ui.home.selection.Camera;
import sti.software.engineering.reading.assistant.ui.home.selection.Component;
import sti.software.engineering.reading.assistant.ui.home.selection.Gallery;

import static sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom.SELECT_CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom.SELECT_GALLERY;

public class ComponentFactory {

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
