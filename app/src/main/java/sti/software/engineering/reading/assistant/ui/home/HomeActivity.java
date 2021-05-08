package sti.software.engineering.reading.assistant.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.adapter.ImageRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;
import sti.software.engineering.reading.assistant.ui.home.sub.HomeFragment;
import sti.software.engineering.reading.assistant.util.Utility;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Next Functionality
 * - revision (on going)
 * - refactor codes
 * <p>
 * https://stackoverflow.com/questions/59965336/calling-method-on-navigation-host-fragment-inside-mainactivity
 * https://www.google.com/search?q=android+call+method+from+activity+to+fragment+with+navigation&sxsrf=ALeKk03RKxMX8_Y1kqigXgbGjkfyLPwvTg%3A1620266059373&ei=S0yTYJjoFci5oATO0oSADw&oq=android+call+method+from+activity+to+fragment+with+navi&gs_lcp=Cgdnd3Mtd2l6EAMYADIHCCEQChCgATIHCCEQChCgAToHCAAQRxCwAzoGCAAQFhAeOgUIIRCgAVCED1jGHmD3JWgBcAJ4AIABuwKIAeUKkgEHNC42LjAuMZgBAKABAaoBB2d3cy13aXrIAQjAAQE&sclient=gws-wiz
 * https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
 */
public class HomeActivity extends BaseActivity implements ImageRecyclerAdapter.OnImageClickListener {

    private static final String TAG = "HomeActivity";

    @Override
    public void onImageClicked(Image image, Uri uri) {
        Log.d(TAG, "IMAGE CLICKED: " + image);
        if (navHostFragment != null) {
            HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            fragment.onImageClicked(image, uri);
        }
    }

    @Inject
    ViewModelProviderFactory providerFactory;

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;
    private NavHostFragment navHostFragment;

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
                if (navHostFragment != null) {
                    HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    fragment.selectImageFromAutoCam();
                    intent.putExtra(TriggerCameraService.INTENT_STARTED_THROUGH_SERVICE, false);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);
        viewModel = new ViewModelProvider(HomeActivity.this, providerFactory).get(HomeViewModel.class);

        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        setSupportActionBar(binding.toolbar);

        subscribeObservers();

        if (!Utility.isMyServiceRunning(this, TriggerCameraService.class)) {
            Intent intent = new Intent(this, TriggerCameraService.class);
            startService(intent);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        openThroughPowerButton();
    }

    private void subscribeObservers() {

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
                if (navHostFragment != null) {
                    HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    fragment.selectImageFromCamera();
                }
            } else if (which == 1) {
                if (checkStoragePermission()) {
                    requestStoragePermission();
                    return;
                }
                if (navHostFragment != null) {
                    HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    fragment.selectImageFromGallery();
                }
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
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri croppedImageUri = result.getUri();

                if (navHostFragment != null) {
                    HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                    fragment.receiveCroppedImage(croppedImageUri);
                }
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
                        if (navHostFragment != null) {
                            HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                            fragment.selectImageFromCamera();
                        }
                    } else viewModel.setShowPermissionRational(true);
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        if (navHostFragment != null) {
                            HomeFragment fragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                            fragment.selectImageFromGallery();
                        }
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