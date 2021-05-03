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
    @ColumnInfo(name = "nickname")
    private String nickname;
    @ColumnInfo(name = "filename")
    private String filename;


    public Image(String nickname, String filename) {
        this.nickname = nickname;
        this.filename = filename;
    }

    @Ignore
    public Image() {
    }

    protected Image(Parcel in) {
        id = in.readInt();
        nickname = in.readString();
        filename = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nickname);
        dest.writeString(filename);
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
                ", nickname='" + nickname + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
