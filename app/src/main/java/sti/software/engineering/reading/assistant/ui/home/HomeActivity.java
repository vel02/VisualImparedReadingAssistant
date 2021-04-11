package sti.software.engineering.reading.assistant.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;
import sti.software.engineering.reading.assistant.ui.home.selection.Camera;
import sti.software.engineering.reading.assistant.ui.home.selection.Gallery;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

/**
 * Next Functionality
 * - Directly open the app through power button.
 */
public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private SelectImageFrom selectImageFrom;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);
        viewModel = new ViewModelProvider(HomeActivity.this, providerFactory).get(HomeViewModel.class);
        setSupportActionBar(binding.toolbar);
    }

    private void selectImageDialog() {
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_select_image);
        builder.setItems(items, ((dialog, which) -> {
            switch (which) {
                case 0:
                    if (!checkCameraPermission())
                        requestCameraPermission();
                    else {
                        selectImageFrom = new SelectImageFrom(new Camera(this));
                        startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
                        imageUri = selectImageFrom.getImageUri();
                    }
                    break;
                case 1:
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        selectImageFrom = new SelectImageFrom(new Gallery());
                        startActivityForResult(selectImageFrom.pickGallery(), IMAGE_PICK_GALLERY_CODE);
                    }
                    break;
            }
        }));
        builder.create().show();
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

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri image = result.getUri();

                binding.imvViewImage.setImageURI(image);

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

            } else if (requestCode == RESULT_CANCELED) {
                if (result != null) {
                    Exception error = result.getError();
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
                }
            }
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
                        selectImageFrom = new SelectImageFrom(new Camera(this));
                        startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
                        imageUri = selectImageFrom.getImageUri();
                    } else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        selectImageFrom = new SelectImageFrom(new Gallery());
                        startActivityForResult(selectImageFrom.pickGallery(), IMAGE_PICK_GALLERY_CODE);
                    } else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}