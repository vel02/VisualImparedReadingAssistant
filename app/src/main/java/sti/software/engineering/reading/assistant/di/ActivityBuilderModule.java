package sti.software.engineering.reading.assistant.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.about.AboutFragmentBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.about.AboutScope;
import sti.software.engineering.reading.assistant.di.ui.accessibility.AccessibilityFragmentBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.accessibility.AccessibilityScope;
import sti.software.engineering.reading.assistant.di.ui.home.HomeDialogBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.home.HomeFragmentBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.home.HomeScope;
import sti.software.engineering.reading.assistant.di.ui.home.HomeViewModelModule;
import sti.software.engineering.reading.assistant.di.ui.picture.PictureFragmentBuilderModule;
import sti.software.engineering.reading.assistant.di.ui.picture.PictureScope;
import sti.software.engineering.reading.assistant.di.ui.picture.PictureViewModelModule;
import sti.software.engineering.reading.assistant.ui.about.AboutActivity;
import sti.software.engineering.reading.assistant.ui.accessibility.AccessibilityActivity;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;
import sti.software.engineering.reading.assistant.ui.picture.PictureActivity;

@Module
public abstract class ActivityBuilderModule {


    @HomeScope
    @ContributesAndroidInjector(modules = {
            HomeDialogBuilderModule.class,
            HomeViewModelModule.class,
            HomeFragmentBuilderModule.class})
    abstract HomeActivity contributeHomeActivity();

    @AccessibilityScope
    @ContributesAndroidInjector(modules = {AccessibilityFragmentBuilderModule.class})
    abstract AccessibilityActivity contributeAccessibilityActivity();

    @AboutScope
    @ContributesAndroidInjector(modules = {AboutFragmentBuilderModule.class})
    abstract AboutActivity contributeActivity();

    @PictureScope
    @ContributesAndroidInjector(modules = {
            PictureViewModelModule.class,
            PictureFragmentBuilderModule.class})
    abstract PictureActivity contributePictureActivity();

}
