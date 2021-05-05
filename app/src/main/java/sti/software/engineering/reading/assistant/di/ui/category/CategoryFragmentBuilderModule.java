package sti.software.engineering.reading.assistant.di.ui.category;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import sti.software.engineering.reading.assistant.di.ui.category.sub.CategoryListScope;
import sti.software.engineering.reading.assistant.ui.category.sub.CategoryListFragment;

@Module
public abstract class CategoryFragmentBuilderModule {

    @CategoryListScope
    @ContributesAndroidInjector
    abstract CategoryListFragment contributeCategoryListFragment();

}
