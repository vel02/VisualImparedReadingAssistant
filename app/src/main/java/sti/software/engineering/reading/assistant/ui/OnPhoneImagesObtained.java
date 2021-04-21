package sti.software.engineering.reading.assistant.ui;

import java.util.Vector;

import sti.software.engineering.reading.assistant.model.PhoneAlbum;

public interface OnPhoneImagesObtained {

    void onComplete(Vector<PhoneAlbum> albums);

    void onError();

}
