package sti.software.engineering.reading.assistant.ui.home;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

    public MutableLiveData<SelectImageFrom> selectImageFrom;
    public MutableLiveData<Boolean> showPermissionRational;
    public MutableLiveData<Uri> saveCroppedImage;

    @Inject
    public HomeViewModel() {
        this.selectImageFrom = new MutableLiveData<>();
        this.showPermissionRational = new MutableLiveData<>();
        this.saveCroppedImage = new MutableLiveData<>();
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

    public enum SelectImageFrom {CAMERA, GALLERY}

}
