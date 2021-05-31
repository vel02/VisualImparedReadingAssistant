package sti.software.engineering.reading.assistant.repository.executor;

import android.util.Log;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.persistence.ImageDatabase;

public class ExecutorDatabase {

    private static final String TAG = "ExecutorDatabase";

    private static final CompositeDisposable disposable = new CompositeDisposable();

    public static void insert(ImageDatabase database, Image image) {
        Observable.just(image).subscribeOn(Schedulers.io()).subscribe(new Observer<Image>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Image image) {
                Log.d(TAG, "SUCCESSFULLY INSERTED");
                database.getImageDao().insertImage(image);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "ERROR INSERTING  ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "DONE INSERTING ");
                disposable.clear();
            }
        });
    }

    public static void update(ImageDatabase database, Image image) {
        Observable.just(image).subscribeOn(Schedulers.io()).subscribe(new Observer<Image>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Image image) {
                Log.d(TAG, "SUCCESSFULLY UPDATED");
                database.getImageDao().updateImage(
                        image.getId(),
                        image.getFilename(),
                        image.getUri(),
                        image.getFile());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "ERROR UPDATING ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "DONE UPDATING");
                disposable.clear();
            }
        });
    }

    public static void delete(ImageDatabase database, Image image) {
        Observable.just(image).subscribeOn(Schedulers.io()).subscribe(new Observer<Image>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(@NonNull Image image) {
                Log.d(TAG, "SUCCESSFULLY DELETED");
                database.getImageDao().deleteImage(image.getId());

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "ERROR DELETING ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "DONE DELETING");
                disposable.clear();
            }
        });
    }


}
