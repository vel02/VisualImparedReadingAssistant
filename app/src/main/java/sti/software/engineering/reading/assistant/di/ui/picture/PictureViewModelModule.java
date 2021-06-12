package sti.software.engineering.reading.assistant.di.ui.picture;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.picture.PictureViewModel;

@Module
public abstract class PictureViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PictureViewModel.class)
    abstract ViewModel bindHomeViewModel(PictureViewModel viewModel);

}
