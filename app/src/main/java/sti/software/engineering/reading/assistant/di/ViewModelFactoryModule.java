package sti.software.engineering.reading.assistant.di;

import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelProviderFactory(ViewModelProviderFactory providerFactory);

}
