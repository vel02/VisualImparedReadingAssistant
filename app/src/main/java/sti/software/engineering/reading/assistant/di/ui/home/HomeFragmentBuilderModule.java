package sti.software.engineering.reading.assistant.di.ui.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.home.camera.CameraFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.gallery.GalleryFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.read.HomeFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.home.read.HomeFragmentViewModelModule;
import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.GalleryFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.read.HomeFragment;

@Module
public abstract class HomeFragmentBuilderModule {

    @HomeFragmentScope
    @ContributesAndroidInjector(modules = {HomeFragmentViewModelModule.class})
    abstract HomeFragment contributeHomeFragment();

    @CameraFragmentScope
    @ContributesAndroidInjector
    abstract CameraFragment contributeCameraFragment();

    @GalleryFragmentScope
    @ContributesAndroidInjector
    abstract GalleryFragment contributeGalleryFragment();

}
