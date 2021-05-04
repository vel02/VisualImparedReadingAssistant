package sti.software.engineering.reading.assistant.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Vector;

import sti.software.engineering.reading.assistant.model.PhoneAlbum;
import sti.software.engineering.reading.assistant.model.PhonePhoto;
import sti.software.engineering.reading.assistant.ui.OnPhoneImagesObtained;

public class DeviceImageManager {

    static class MediaColumns {
        public static final String _ID = "_id";
        public static final String DATA = "_data";
        public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
    }

    public static void getPhoneAlbums(Context context, OnPhoneImagesObtained listener) {
        // Creating vectors to hold the final albums objects and albums names
        Vector<PhoneAlbum> phoneAlbums = new Vector<>();
        Vector<String> albumsNames = new Vector<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaColumns.BUCKET_DISPLAY_NAME,
                MediaColumns.DATA,
                MediaColumns._ID
        };

        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur != null && cur.getCount() > 0) {
            Log.i("DeviceImageManager", " query count=" + cur.getCount());

            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaColumns.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cur.getColumnIndex(
                        MediaColumns.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaColumns._ID);

                do {
                    // Get the field values
                    bucketName = cur.getString(bucketNameColumn);
                    data = cur.getString(imageUriColumn);
                    imageId = cur.getString(imageIdColumn);

                    // Adding a new PhonePhoto object to phonePhotos vector
                    PhonePhoto phonePhoto = new PhonePhoto();
                    phonePhoto.setAlbumName(bucketName);
                    phonePhoto.setPhotoUri(data);
                    phonePhoto.setId(Integer.parseInt(imageId));

                    if (albumsNames.contains(bucketName)) {
                        for (PhoneAlbum album : phoneAlbums) {
                            if (album.getName().equals(bucketName)) {
                                album.getAlbumPhotos().add(phonePhoto);
                                Log.i("DeviceImageManager", "A photo was added to album => " + bucketName);
                                break;
                            }
                        }
                    } else {
                        PhoneAlbum album = new PhoneAlbum();
                        Log.i("DeviceImageManager", "A new album was created => " + bucketName);
                        album.setId(phonePhoto.getId());
                        album.setName(bucketName);
                        album.setCoverUri(phonePhoto.getPhotoUri());
                        album.getAlbumPhotos().add(phonePhoto);
                        Log.i("DeviceImageManager", "A photo was added to album => " + bucketName);

                        phoneAlbums.add(album);
                        albumsNames.add(bucketName);
                    }

                } while (cur.moveToNext());
            }

            cur.close();
            listener.onComplete(phoneAlbums);
        } else {
            listener.onError();
        }
    }
}

//testing image retrieving
//REFERENCE: https://stackoverflow.com/questions/4195660/get-list-of-photo-galleries-on-android
//BY: BARAKUDA
//        DeviceImageManager.getPhoneAlbums(this, new OnPhoneImagesObtained() {
//            @Override
//            public void onComplete(Vector<PhoneAlbum> albums) {
//                for (int i = 0; i < albums.size(); i++) {
//                    PhoneAlbum album = albums.get(i);
//                    Log.d(TAG, "ALBUM NAME: " + album.getName());
//                    Log.d(TAG, "ALBUM ID: " + album.getId());
//
//                    if (album.getName().equals("VisualImpairedImages")) {
//                        Vector<PhonePhoto> phonePhotos = album.getAlbumPhotos();
//                        for (int j = 0; j < phonePhotos.size(); j++) {
//                            PhonePhoto photo = phonePhotos.get(j);
//                            Log.d(TAG, "PHOTO URI: " + photo.getPhotoUri());
////                            Picasso.get()
////                                    .load("file:" + photo.getPhotoUri())
////                                    .centerCrop()
////                                    .fit()
////                                    .into(binding.imvViewImage);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });
