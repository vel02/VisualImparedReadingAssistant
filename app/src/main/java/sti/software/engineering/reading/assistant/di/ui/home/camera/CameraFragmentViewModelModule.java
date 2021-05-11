package sti.software.engineering.reading.assistant.di.ui.home.camera;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragmentViewModel;

@Module
public abstract class CameraFragmentViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CameraFragmentViewModel.class)
    abstract ViewModel bindCameraViewModel(CameraFragmentViewModel viewModel);

}
