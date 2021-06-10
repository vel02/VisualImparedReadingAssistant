package sti.software.engineering.reading.assistant.di.ui.about;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.about.about.AboutFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.contactus.ContactUsFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.InstructionsFragmentScope;
import sti.software.engineering.reading.assistant.ui.about.sub.AboutFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.ContactUsFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.InstructionsFragment;

@Module
public abstract class AboutFragmentBuilderModule {

    @AboutFragmentScope
    @ContributesAndroidInjector
    abstract AboutFragment contributeAboutFragment();

    @InstructionsFragmentScope
    @ContributesAndroidInjector
    abstract InstructionsFragment contributeInstructionFragment();

    @ContactUsFragmentScope
    @ContributesAndroidInjector
    abstract ContactUsFragment contributeContactUsFragment();

}
