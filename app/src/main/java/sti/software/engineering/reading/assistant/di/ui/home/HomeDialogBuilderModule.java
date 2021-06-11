package sti.software.engineering.reading.assistant.di.ui.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.home.dialog.HomeDeleteImagesDialogScope;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.dialog.DeletingImagesDialog;

@Module
public abstract class HomeDialogBuilderModule {

    @HomeDeleteImagesDialogScope
    @ContributesAndroidInjector
    abstract DeletingImagesDialog contributeDeletingImagesDialog();

}
