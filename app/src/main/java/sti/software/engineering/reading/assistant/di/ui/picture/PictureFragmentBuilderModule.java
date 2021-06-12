package sti.software.engineering.reading.assistant.di.ui.picture;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.picture.sub.PictureFragmentScope;
import sti.software.engineering.reading.assistant.ui.picture.sub.PictureFragment;

@Module
public abstract class PictureFragmentBuilderModule {

    @PictureFragmentScope
    @ContributesAndroidInjector
    abstract PictureFragment contributePictureFragment();

}
