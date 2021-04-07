package sti.software.engineering.reading.assistant.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import sti.software.engineering.reading.assistant.BaseApplication;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ViewModelFactoryModule.class,
                ActivityBuilderModule.class,
                AppModule.class
        }
)
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}
