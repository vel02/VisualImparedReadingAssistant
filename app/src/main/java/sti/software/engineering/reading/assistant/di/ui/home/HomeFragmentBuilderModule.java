package sti.software.engineering.reading.assistant.di.ui.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.home.camera.CameraFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.camera.CameraFragmentViewModelModule;
import sti.software.engineering.reading.assistant.di.ui.home.gallery.GalleryFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.read.ReadFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.read.ReadFragmentViewModelModule;
import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.GalleryFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragment;

@Module
public abstract class HomeFragmentBuilderModule {

    @ReadFragmentScope
    @ContributesAndroidInjector(modules = {ReadFragmentViewModelModule.class})
    abstract ReadFragment contributeReadFragment();

    @CameraFragmentScope
    @ContributesAndroidInjector(modules = {CameraFragmentViewModelModule.class})
    abstract CameraFragment contributeCameraFragment();

    @GalleryFragmentScope
    @ContributesAndroidInjector
    abstract GalleryFragment contributeGalleryFragment();

}
