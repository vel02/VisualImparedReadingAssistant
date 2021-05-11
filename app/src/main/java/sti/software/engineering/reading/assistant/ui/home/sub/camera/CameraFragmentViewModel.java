package sti.software.engineering.reading.assistant.ui.home.sub.camera;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

public class CameraFragmentViewModel extends ViewModel {

    private final MutableLiveData<Uri> imageUriLiveData;
    private final MutableLiveData<Uri> storeCroppedImageLiveData;


    private final ImageRepository repository;

    @Inject
    public CameraFragmentViewModel(ImageRepository repository) {
        this.repository = repository;
        this.imageUriLiveData = new MutableLiveData<>();
        this.storeCroppedImageLiveData = new MutableLiveData<>();
    }

    public void insert(Image image) {
        this.repository.insert(image);
    }

    public void storeCroppedImage(Uri uri) {
        this.storeCroppedImageLiveData.setValue(uri);
    }

    public LiveData<Uri> observedStoreCroppedImage() {
        return storeCroppedImageLiveData;
    }

    public void setImageUriLiveData(Uri uri) {
        this.imageUriLiveData.setValue(uri);
    }

    public LiveData<Uri> observedImageUri() {
        return this.imageUriLiveData;
    }

}
