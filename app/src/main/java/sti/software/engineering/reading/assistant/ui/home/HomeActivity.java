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
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.adapter.ImageRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;
import sti.software.engineering.reading.assistant.ui.OnHostPermissionListener;
import sti.software.engineering.reading.assistant.ui.home.sub.PagerAdapter;
import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragment;
import sti.software.engineering.reading.assistant.util.Utility;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Next Functionality
 * - permission
 * - refactor codes
 * - camera tab implementation
 * <p>
 * https://stackoverflow.com/questions/59965336/calling-method-on-navigation-host-fragment-inside-mainactivity
 * https://www.google.com/search?q=android+call+method+from+activity+to+fragment+with+navigation&sxsrf=ALeKk03RKxMX8_Y1kqigXgbGjkfyLPwvTg%3A1620266059373&ei=S0yTYJjoFci5oATO0oSADw&oq=android+call+method+from+activity+to+fragment+with+navi&gs_lcp=Cgdnd3Mtd2l6EAMYADIHCCEQChCgATIHCCEQChCgAToHCAAQRxCwAzoGCAAQFhAeOgUIIRCgAVCED1jGHmD3JWgBcAJ4AIABuwKIAeUKkgEHNC42LjAuMZgBAKABAaoBB2d3cy13aXrIAQjAAQE&sclient=gws-wiz
 * https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
 * <p>
 * https://stackoverflow.com/questions/38471105/viewpager-didnt-load-the-first-page-at-the-first-time-but-will-load-it-after-s
 */
public class HomeActivity extends BaseActivity implements
        OnHostPermissionListener,
        ImageRecyclerAdapter.OnImageClickListener {

    private static final String TAG = "HomeActivity";

    @Override
    public void onImageClicked(Image image, Uri uri) {
        Log.d(TAG, "IMAGE CLICKED: " + image);
        if (getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem()) instanceof ReadFragment) {
            ReadFragment fragment = (ReadFragment) getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem());
            fragment.onImageClicked(image, uri);
        }
    }

    @Override
    public boolean onCheckCameraPermission() {
        return this.checkCameraPermission();
    }

    @Override
    public void onRequestCameraPermission() {
        this.requestCameraPermission();
    }

    @Inject
    ViewModelProviderFactory providerFactory;

    private ActivityHomeBinding binding;
    private HomeViewModel viewModel;

    private boolean startedThroughService;

    private OnStartThroughServiceListener startThroughServiceListener;

    public interface OnStartThroughServiceListener {
        void onStartedFromService();
    }

    public void setOnStartThroughServiceListener(OnStartThroughServiceListener listener) {
        this.startThroughServiceListener = listener;
    }

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

                this.startedThroughService = true;
                intent.putExtra(TriggerCameraService.INTENT_STARTED_THROUGH_SERVICE, false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);
        viewModel = new ViewModelProvider(HomeActivity.this, providerFactory).get(HomeViewModel.class);

        setSupportActionBar(binding.toolbar);
        initViewPager();

        subscribeObservers();

        if (!Utility.isMyServiceRunning(this, TriggerCameraService.class)) {
            Intent intent = new Intent(this, TriggerCameraService.class);
            startService(intent);
        }
    }

    private void initViewPager() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Read"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Camera"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Gallery"));

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setUserInputEnabled(false);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected: " + tab.getPosition());
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
                Log.d(TAG, "onPageSelected: called " + position + " " + startedThroughService);
                if (position == 0 && startedThroughService) {
                    Log.d(TAG, "onPageSelected: called");
                    startThroughServiceListener.onStartedFromService();
                    startedThroughService = false;
                }
            }
        });
        Log.d(TAG, "initViewPager: count " + getSupportFragmentManager().getFragments().size());

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

                if (getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem()) instanceof ReadFragment) {
                    ReadFragment fragment = (ReadFragment) getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem());
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
        if (item.getItemId() == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "onRequestPermissionsResult: camera should work!" + (cameraAccepted == storageAccepted));
                if (cameraAccepted && storageAccepted) {
                    if (getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem()) instanceof CameraFragment) {
                        CameraFragment fragment = (CameraFragment) getSupportFragmentManager().getFragments().get(binding.viewPager.getCurrentItem());
                        fragment.selectImageFromCamera();
                    }
                } else viewModel.setShowPermissionRational(true);
            }
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

}