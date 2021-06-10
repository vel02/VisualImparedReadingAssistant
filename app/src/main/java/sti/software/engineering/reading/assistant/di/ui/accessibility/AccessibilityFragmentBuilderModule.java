package sti.software.engineering.reading.assistant.di.ui.accessibility;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.accessibility.accessibility.AccessibilityFragmentScope;
import sti.software.engineering.reading.assistant.ui.accessibility.sub.AccessibilityFragment;

@Module
public abstract class AccessibilityFragmentBuilderModule {

    @AccessibilityFragmentScope
    @ContributesAndroidInjector
    abstract AccessibilityFragment contributeAccessibilityFragment();

}
