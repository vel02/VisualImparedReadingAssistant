package sti.software.engineering.reading.assistant.di.ui.home.gallery;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.GalleryFragmentViewModel;

@Module
public abstract class GalleryFragmentViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GalleryFragmentViewModel.class)
    abstract ViewModel bindGalleryFragmentViewModel(GalleryFragmentViewModel viewModel);

}
