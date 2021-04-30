package sti.software.engineering.reading.assistant.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.Utility;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static sti.software.engineering.reading.assistant.ui.home.HomeViewModel.SelectImageFrom.AUTO_CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.HomeViewModel.SelectImageFrom.CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.HomeViewModel.SelectImageFrom.GALLERY;

/**
 * Next Functionality
 * - database (SQLite or Room)
 * - Save button support
 * - Retake Image options
 * - Speech Command integration
 * - refactor codes
 * <p>
 * Stand by.
 * - display photos through recycler view.
 * - refactor codes
 */
public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    private ActivityHomeBinding binding;

    private HomeViewModel viewModel;
    private SelectImageFrom selectImageFrom;

    private Uri imageUri;
    private File capturedImage;
    private String filename;

    private void openThroughPowerButton() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean startedThroughService = intent
                    .getBooleanExtra(TriggerCameraService.INTENT_STARTED_THROUGH_SERVICE, false);
            if (startedThroughService) {
                if (checkCameraPermission()) {
                    viewModel.setShowPermissionRational(true);
                    return;
                }
                viewModel.setSelectImageFrom(AUTO_CAMERA);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);
        viewModel = new ViewModelProvider(HomeActivity.this, providerFactory).get(HomeViewModel.class);
        setSupportActionBar(binding.toolbar);
        subscribeObservers();

        if (!Utility.isMyServiceRunning(this, TriggerCameraService.class)) {
            Intent intent = new Intent(this, TriggerCameraService.class);
            startService(intent);
        }

        openThroughPowerButton();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.processDatabaseData();

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
    }

    private void subscribeObservers() {

        viewModel.observedSelectedImage().observe(this, selectImage -> {

            switch (selectImage) {
                case AUTO_CAMERA:
                    selectImageFrom = new SelectImageFrom(this, SelectImageFrom.SELECT_CAMERA);
                    startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_AUTO_CAMERA_CODE);
                    imageUri = selectImageFrom.getImageUri();
                    capturedImage = selectImageFrom.getFile();
                    filename = selectImageFrom.getFilename();
                    break;

                case CAMERA:
                    selectImageFrom = new SelectImageFrom(this, SelectImageFrom.SELECT_CAMERA);
                    startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
                    imageUri = selectImageFrom.getImageUri();
                    capturedImage = selectImageFrom.getFile();
                    filename = selectImageFrom.getFilename();
                    break;

                case GALLERY:
                    selectImageFrom = new SelectImageFrom(this, SelectImageFrom.SELECT_GALLERY);
                    startActivityForResult(selectImageFrom.pickGallery(), IMAGE_PICK_GALLERY_CODE);
                    break;
            }

        });

        viewModel.observedShowPermissionRational().observe(this, showPermissionRational -> {
            if (showPermissionRational) {
                Snackbar.make(binding.getRoot(), R.string.label_permission_rational, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.label_grant_permission, v -> {
                            Intent permissionIntent = new Intent();
                            permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    this.getPackageName(), null);
                            permissionIntent.setData(uri);
                            permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(permissionIntent);
                        }).show();
            }
        });

        viewModel.setSaveCroppedImage(null);
        viewModel.observedSaveCroppedImage().observe(this, croppedImageUri -> {
            if (croppedImageUri != null) {
                new Thread(() -> {
                    File capturedImageFile = this.capturedImage;
                    if (capturedImageFile == null) return;

                    try {
                        Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), croppedImageUri);
                        FileOutputStream fileOutputStream = new FileOutputStream(capturedImageFile);
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Utility.refreshGallery(this, filename);
                }).start();
            }
        });

        viewModel.setExtractText(false);
        viewModel.observedExtractText().observe(this, extract -> {
            if (extract) {
                //text recognition processes
                BitmapDrawable drawable = (BitmapDrawable) binding.imvViewImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer
                        .Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Cannot recognize text", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock item = items.valueAt(i);
                        sb.append(item.getValue());
                        sb.append("\n");
                    }

                    Log.d(TAG, "result below:\n" + sb.toString());
                }
            }
        });

        viewModel.observedImages().observe(this, images -> {
            if (images != null) {
                Log.d(TAG, "Images: " + images);
                for (int i = 0; i < images.size(); i++) {
                    Image image = images.get(i);
                    File file = image.getFileObject();
                    Log.d(TAG, "Image: " + image.toString());
                    Log.d(TAG, "File: " + file.getPath());
                }
            }
        });

    }

    private void selectImageDialog() {
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_select_image);
        builder.setItems(items, ((dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    requestCameraPermission();
                    return;
                }
                viewModel.setSelectImageFrom(CAMERA);
            } else if (which == 1) {
                if (checkStoragePermission()) {
                    requestStoragePermission();
                    return;
                }
                viewModel.setSelectImageFrom(GALLERY);
            }
        }));
        builder.create().show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindTriggerCameraService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindTriggerCameraService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

            if (requestCode == IMAGE_PICK_AUTO_CAMERA_CODE) {

                //display
                binding.imvViewImage.setImageURI(imageUri);

                //extract text
                viewModel.setExtractText(true);

                //testing save image
                Image image = new Image(filename);
                viewModel.insert(image);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                //retrieve
                Uri croppedImageUri = result.getUri();

                //save cropped image to app folder, replacing the initial image.
                //should be on the background/thread
                viewModel.setSaveCroppedImage(croppedImageUri);

                //display
                binding.imvViewImage.setImageURI(croppedImageUri);

                //text recognition processes
                viewModel.setExtractText(true);

            }
        }
    }

    private void bindTriggerCameraService() {
        bindService(new Intent(this, TriggerCameraService.class),
                connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindTriggerCameraService() {
        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_select_image) {
            selectImageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        viewModel.setSelectImageFrom(CAMERA);
                    } else viewModel.setShowPermissionRational(true);
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        viewModel.setSelectImageFrom(GALLERY);
                    } else viewModel.setShowPermissionRational(true);
                }
                break;
        }
    }

    private boolean checkCameraPermission() {
        return (PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(this, Manifest.permission.CAMERA)
                || PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(this, WRITE_EXTERNAL_STORAGE));
    }

    private void requestCameraPermission() {
        boolean shouldProvideRational = (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE));

        if (shouldProvideRational) {
            Snackbar.make(binding.getRoot(),
                    R.string.label_permission_rational, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_ok, v ->
                            ActivityCompat.requestPermissions(this, new String[]{
                                            Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE},
                                    CAMERA_REQUEST_CODE)).show();
        } else ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE},
                CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        return PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
    }

    private void requestStoragePermission() {
        boolean shouldProvideRational = ActivityCompat
                .shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE);

        if (shouldProvideRational) {
            Snackbar.make(binding.getRoot(),
                    R.string.label_permission_rational, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_ok, v ->
                            ActivityCompat.requestPermissions(this,
                                    new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE)).show();
        } else ActivityCompat.requestPermissions(this,
                new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
    }


}