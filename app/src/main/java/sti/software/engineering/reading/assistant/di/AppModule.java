package sti.software.engineering.reading.assistant.di;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sti.software.engineering.reading.assistant.persistence.ImageDatabase;
import sti.software.engineering.reading.assistant.repository.ImageRepository;

@Module
public class AppModule {

    @Singleton
    @Provides
    static ImageDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(
                application, ImageDatabase.class,
                ImageDatabase.DATABASE_NAME).build();
    }

    @Singleton
    @Provides
    static ImageRepository provideImageRepository(ImageDatabase database) {
        return new ImageRepository(database);
    }

}
