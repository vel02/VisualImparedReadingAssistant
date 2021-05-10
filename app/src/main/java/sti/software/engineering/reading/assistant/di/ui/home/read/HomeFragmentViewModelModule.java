package sti.software.engineering.reading.assistant.di.ui.home.read;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.home.sub.read.HomeFragmentViewModel;

@Module
public abstract class HomeFragmentViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel.class)
    abstract ViewModel bindHomeFragmentViewModel(HomeFragmentViewModel viewModel);

}
