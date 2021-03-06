package sti.software.engineering.reading.assistant.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import sti.software.engineering.reading.assistant.model.Image;

@Dao
public interface ImageDao {

    @Insert
    void insertImage(Image image);

    @Query("UPDATE images SET filename = :filename, uri = :uri, file = :file, selected = :selected " +
            "WHERE id = :id")
    void updateImage(int id, String filename, String uri, String file, boolean selected);

    @Query("DELETE FROM images WHERE id = :id")
    void deleteImage(int id);

    @Query("SELECT * FROM images ORDER BY id DESC")
    LiveData<List<Image>> selectImages();

}
