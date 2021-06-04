package sti.software.engineering.reading.assistant.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Boolean> showPermissionRational;

    @Inject
    public HomeViewModel() {
        this.showPermissionRational = new MutableLiveData<>();
    }

    public void setShowPermissionRational(boolean showPermissionRational) {
        this.showPermissionRational.setValue(showPermissionRational);
    }

    public LiveData<Boolean> observedShowPermissionRational() {
        return showPermissionRational;
    }

}
