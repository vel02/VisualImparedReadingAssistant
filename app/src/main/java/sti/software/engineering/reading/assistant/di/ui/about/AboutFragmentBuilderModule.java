package sti.software.engineering.reading.assistant.di.ui.about;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.about.about.AboutFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.contactus.ContactUsFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.InstructionsFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.sub.InstructionFourFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.sub.InstructionOneFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.sub.InstructionThreeFragmentScope;
import sti.software.engineering.reading.assistant.di.ui.about.instructions.sub.InstructionTwoFragmentScope;
import sti.software.engineering.reading.assistant.ui.about.sub.ContactUsFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.about.AboutFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.InstructionsFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionFourFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionOneFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionThreeFragment;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionTwoFragment;

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


    @InstructionOneFragmentScope
    @ContributesAndroidInjector
    abstract InstructionOneFragment contributeInstructionOneFragment();

    @InstructionTwoFragmentScope
    @ContributesAndroidInjector
    abstract InstructionTwoFragment contributeInstructionTwoFragment();

    @InstructionThreeFragmentScope
    @ContributesAndroidInjector
    abstract InstructionThreeFragment contributeInstructionThreeFragment();

    @InstructionFourFragmentScope
    @ContributesAndroidInjector
    abstract InstructionFourFragment contributeInstructionFourFragment();


}
