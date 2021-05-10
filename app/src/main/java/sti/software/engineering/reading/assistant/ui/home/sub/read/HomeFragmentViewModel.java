package sti.software.engineering.reading.assistant.ui.home.sub.read;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

public class HomeFragmentViewModel extends ViewModel {

    public MutableLiveData<SelectImageFrom> selectImageFrom;
    public MutableLiveData<Uri> storeCroppedImage;
    public MutableLiveData<Boolean> extractText;

    private final ImageRepository repository;

    @Inject
    public HomeFragmentViewModel(ImageRepository repository) {
        this.repository = repository;
        this.selectImageFrom = new MutableLiveData<>();
        this.storeCroppedImage = new MutableLiveData<>();
        this.extractText = new MutableLiveData<>();
    }

    public void processDatabaseData() {
        this.repository.select();
    }

    public void insert(Image image) {
        this.repository.insert(image);
    }

    public void update(Image image) {
        this.repository.update(image);
    }

    public void delete(Image image) {
        this.repository.delete(image);
    }

    public LiveData<List<Image>> observedImages() {
        return this.repository.getImages();
    }

    public void setExtractText(boolean extract) {
        this.extractText.setValue(extract);
    }

    public LiveData<Boolean> observedExtractText() {
        return extractText;
    }

    public void storeCroppedImage(Uri uri) {
        this.storeCroppedImage.setValue(uri);
    }

    public LiveData<Uri> observedStoreCroppedImage() {
        return storeCroppedImage;
    }

    public void setSelectImageFrom(SelectImageFrom selectImageFrom) {
        this.selectImageFrom.setValue(selectImageFrom);
    }

    public LiveData<SelectImageFrom> observedSelectedImage() {
        return selectImageFrom;
    }

    public enum SelectImageFrom {AUTO_CAMERA, CAMERA, GALLERY}
}
