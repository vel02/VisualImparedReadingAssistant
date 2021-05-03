package sti.software.engineering.reading.assistant.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.persistence.ImageDatabase;
import sti.software.engineering.reading.assistant.repository.executor.ExecutorDatabase;

public class ImageRepository {

    private static final String TAG = "ImageRepository";

    private final ImageDatabase database;

    private final MediatorLiveData<List<Image>> images = new MediatorLiveData<>();

    @Inject
    public ImageRepository(ImageDatabase database) {
        this.database = database;
    }

    public void insert(Image image) {
        Log.d(TAG, "insert: called");
        ExecutorDatabase.insert(database, image);
    }

    public void update(Image image) {
        ExecutorDatabase.update(database, image);
    }

    public void delete(Image image) {
        ExecutorDatabase.delete(database, image);
    }

    public void select() {
        final LiveData<List<Image>> source = database.getImageDao().selectImages();

        images.addSource(source, imagesList -> {
            if (imagesList != null) {
                ImageRepository.this.images.setValue(imagesList);
            }
            ImageRepository.this.images.removeSource(source);
        });
    }

    public LiveData<List<Image>> getImages() {
        return images;
    }

}
