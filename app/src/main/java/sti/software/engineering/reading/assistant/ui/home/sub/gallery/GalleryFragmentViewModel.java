package sti.software.engineering.reading.assistant.ui.home.sub.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

public class GalleryFragmentViewModel extends ViewModel {

    private final ImageRepository repository;

    @Inject
    public GalleryFragmentViewModel(ImageRepository repository) {
        this.repository = repository;
    }

    public void processDatabaseData() {
        this.repository.select();
    }

    public void removeImage(Image image) {
        this.repository.delete(image);
    }

    public LiveData<List<Image>> observedImages() {
        return this.repository.getImages();
    }

}
