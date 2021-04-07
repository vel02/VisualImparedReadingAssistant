package sti.software.engineering.reading.assistant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.home.HomeScope;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;

@Module
public abstract class ActivityBuilderModule {


    @HomeScope
    @ContributesAndroidInjector
    abstract HomeActivity contributeHomeActivity();


}
