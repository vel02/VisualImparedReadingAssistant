package sti.software.engineering.reading.assistant.ui.home;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

public class HomeViewModel extends ViewModel {

    public MutableLiveData<SelectImageFrom> selectImageFrom;
    public MutableLiveData<Boolean> showPermissionRational;
    public MutableLiveData<Uri> saveCroppedImage;
    public MutableLiveData<Boolean> extractText;

    @Inject
    ImageRepository repository;

    @Inject
    public HomeViewModel() {
        this.selectImageFrom = new MutableLiveData<>();
        this.showPermissionRational = new MutableLiveData<>();
        this.saveCroppedImage = new MutableLiveData<>();
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

    public void setSaveCroppedImage(Uri uri) {
        this.saveCroppedImage.setValue(uri);
    }

    public LiveData<Uri> observedSaveCroppedImage() {
        return saveCroppedImage;
    }

    public void setShowPermissionRational(boolean showPermissionRational) {
        this.showPermissionRational.setValue(showPermissionRational);
    }

    public LiveData<Boolean> observedShowPermissionRational() {
        return showPermissionRational;
    }

    public void setSelectImageFrom(SelectImageFrom selectImageFrom) {
        this.selectImageFrom.setValue(selectImageFrom);
    }

    public LiveData<SelectImageFrom> observedSelectedImage() {
        return selectImageFrom;
    }

    public enum SelectImageFrom {AUTO_CAMERA, CAMERA, GALLERY}

}
