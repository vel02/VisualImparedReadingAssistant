package sti.software.engineering.reading.assistant.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StoreCroppedImageManager {

    private static final String TAG = "StoreCroppedImageManage";

    private static final CompositeDisposable disposable = new CompositeDisposable();

    public static void storeImage(Context context, File file, Uri uri, String filename) {
        Observable.just(file).subscribeOn(Schedulers.io())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull File file) {
                        try {
                            Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Utility.refreshGallery(context, filename);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "ERROR STORING IMAGE");
                    }

                    @Override
                    public void onComplete() {
                        disposable.clear();
                    }
                });
    }

}
