package sti.software.engineering.reading.assistant.model;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.File;

@Entity(tableName = "images")
public class Image implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "filename")
    private String filename;
    @ColumnInfo(name = "uri")
    private String uri;
    @ColumnInfo(name = "file")
    private String file;


    public Image(String filename, String uri, String file) {
        this.filename = filename;
        this.uri = uri;
        this.file = file;
    }

    @Ignore
    public Image() {
    }

    protected Image(Parcel in) {
        id = in.readInt();
        filename = in.readString();
        uri = in.readString();
        file = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(filename);
        dest.writeString(uri);
        dest.writeString(file);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public File getFileObject() {
        final String SEPARATOR = File.separator;
        final String FOLDER_NAME = "VisualImpairedImages";
        String external_storage_directory = Environment.getExternalStorageDirectory().toString();

        String pathname = external_storage_directory + SEPARATOR + "Pictures"
                + SEPARATOR + FOLDER_NAME + SEPARATOR + filename;

        return new File(pathname);
    }

    @Override
    @NonNull
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", uri='" + uri + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
