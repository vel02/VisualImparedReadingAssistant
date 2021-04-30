package sti.software.engineering.reading.assistant.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import sti.software.engineering.reading.assistant.model.Image;

@Database(entities = {Image.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "note_db";

    public abstract ImageDao getImageDao();

}
