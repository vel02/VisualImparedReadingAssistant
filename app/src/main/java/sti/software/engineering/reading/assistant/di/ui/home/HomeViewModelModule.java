package sti.software.engineering.reading.assistant.di.ui.home;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.home.HomeViewModel;

@Module
public abstract class HomeViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

}
