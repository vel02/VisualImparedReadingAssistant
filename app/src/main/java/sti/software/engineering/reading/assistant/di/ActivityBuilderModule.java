package sti.software.engineering.reading.assistant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.category.CategoryFragmentBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.category.CategoryScope;
import sti.software.engineering.reading.assistant.di.ui.home.HomeScope;
import sti.software.engineering.reading.assistant.di.ui.home.HomeViewModelModule;
import sti.software.engineering.reading.assistant.ui.category.CategoryActivity;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;

@Module
public abstract class ActivityBuilderModule {


    @HomeScope
    @ContributesAndroidInjector(modules = {HomeViewModelModule.class})
    abstract HomeActivity contributeHomeActivity();

    @CategoryScope
    @ContributesAndroidInjector(modules = {CategoryFragmentBuilderModule.class})
    abstract CategoryActivity contributeCategoryActivity();

}
