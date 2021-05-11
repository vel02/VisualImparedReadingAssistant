package sti.software.engineering.reading.assistant.di.ui.home.read;

import androidx.lifecycle.ViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import sti.software.engineering.reading.assistant.di.ViewModelKey;
import sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragmentViewModel;

@Module
public abstract class ReadFragmentViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReadFragmentViewModel.class)
    abstract ViewModel bindReadFragmentViewModel(ReadFragmentViewModel viewModel);

}
