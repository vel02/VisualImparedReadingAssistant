package sti.software.engineering.reading.assistant.di.ui.home.sub;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.ui.home.sub.HomeFragment;

@Module
public abstract class HomeFragmentBuilderModule {

    @HomeFragmentScope
    @ContributesAndroidInjector(modules = {HomeFragmentViewModelModule.class})
    abstract HomeFragment contributeHomeFragment();

}
