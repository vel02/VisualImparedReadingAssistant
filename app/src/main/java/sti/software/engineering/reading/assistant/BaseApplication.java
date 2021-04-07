package sti.software.engineering.reading.assistant;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import sti.software.engineering.reading.assistant.di.DaggerAppComponent;

public class BaseApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
