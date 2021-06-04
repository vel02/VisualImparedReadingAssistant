package sti.software.engineering.reading.assistant.ui.home.sub.read;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

public class ReadFragmentViewModel extends ViewModel {

    private final MutableLiveData<UtteranceProgress> utteranceProgressLiveData;
    private final MutableLiveData<SelectImageFrom> selectImageFromLiveData;
    private final MutableLiveData<Boolean> extractTextLiveData;
    private final MutableLiveData<Boolean> buttonStopStateLiveData;
    private final MutableLiveData<Boolean> buttonReadStateLiveData;

    private final ImageRepository repository;

    @Inject
    public ReadFragmentViewModel(ImageRepository repository) {
        this.repository = repository;
        this.utteranceProgressLiveData = new MutableLiveData<>();
        this.selectImageFromLiveData = new MutableLiveData<>();
        this.extractTextLiveData = new MutableLiveData<>();
        this.buttonReadStateLiveData = new MutableLiveData<>();
        this.buttonStopStateLiveData = new MutableLiveData<>();
    }

    public void processDatabaseData() {
        this.repository.select();
    }

    public void insert(Image image) {
        this.repository.insert(image);
    }

    public LiveData<List<Image>> observedImages() {
        return this.repository.getImages();
    }

    public void setSelectImageFrom(SelectImageFrom selectImageFromLiveData) {
        this.selectImageFromLiveData.setValue(selectImageFromLiveData);
    }

    public LiveData<SelectImageFrom> observedSelectedImage() {
        return selectImageFromLiveData;
    }

    public void setExtractText(boolean extract) {
        this.extractTextLiveData.setValue(extract);
    }

    public void postExtractText(boolean extract) {
        this.extractTextLiveData.postValue(extract);
    }

    public LiveData<Boolean> observedExtractText() {
        return extractTextLiveData;
    }

    public void setButtonReadState(boolean enable) {
        this.buttonReadStateLiveData.setValue(enable);
    }

    public void postButtonReadState(boolean enable) {
        this.buttonReadStateLiveData.postValue(enable);
    }

    public LiveData<Boolean> observedButtonReadState() {
        return buttonReadStateLiveData;
    }

    public void setButtonStopState(boolean enable) {
        this.buttonStopStateLiveData.setValue(enable);
    }

    public void postButtonStopState(boolean enable) {
        this.buttonStopStateLiveData.postValue(enable);
    }

    public LiveData<Boolean> observedButtonStopState() {
        return buttonStopStateLiveData;
    }

    public void setUtteranceProgress(UtteranceProgress progress) {
        this.utteranceProgressLiveData.setValue(progress);
    }

    public void postUtteranceProgress(UtteranceProgress progress) {
        this.utteranceProgressLiveData.postValue(progress);
    }

    public LiveData<UtteranceProgress> observedUtteranceProgress() {
        return this.utteranceProgressLiveData;
    }

    public enum SelectImageFrom {AUTO_CAMERA}

    public enum UtteranceProgress {UTTERANCE_START_READING, UTTERANCE_DONE_READING}
}
