package sti.software.engineering.reading.assistant.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import javax.inject.Inject;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.adapter.ImageRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;
import sti.software.engineering.reading.assistant.ui.OnHostPermissionListener;
import sti.software.engineering.reading.assistant.ui.accessibility.AccessibilityActivity;
import sti.software.engineering.reading.assistant.ui.home.sub.PagerAdapter;
import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragment;
import sti.software.engineering.reading.assistant.util.Utility;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Next Functionality
 * - rename file
 * - file description add more information (directory, uri)
 * - camera tab implementation
 * <p>
 * https://stackoverflow.com/questions/36369913/how-to-implement-multi-select-in-recyclerview
 * <p>
 * https://stackoverflow.com/questions/10424997/android-how-to-rename-a-file
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
    private Menu menu;

    private boolean startedThroughService;

    private OnStartThroughServiceListener startThroughServiceListener;

    public interface OnStartThroughServiceListener {
        void onStartedFromService();
    }

    public void setOnStartThroughServiceListener(OnStartThroughServiceListener listener) {
        this.startThroughServiceListener = listener;
    }

//    private OnVoiceChangeListener listener;
//
//    public interface OnVoiceChangeListener {
//        void onVoiceChanged();
//    }
//
//    public void setOnVoiceChangeListener(OnVoiceChangeListener listener) {
//        this.listener = listener;
//    }

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
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("View"));
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

        //permission temporary location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(this)) {
            Snackbar.make(binding.getRoot(), "Allow \"Display over other app\" to grant camera access in the background", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        requestPermission();
                    }).show();
        }
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


    //https://stackoverflow.com/questions/59419653/cannot-start-activity-background-in-android-10-android-q
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323;

    private void requestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Log.d(TAG, "onActivityResult: permission denied");
                } else {
                    // Permission Granted-System will work
                    Log.d(TAG, "onActivityResult: permission granted");
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
        if (item.getItemId() == R.id.action_accessibility) {
            Intent intent = new Intent(this, AccessibilityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
//        if (item.getItemId() == R.id.action_accessibility) {
//            Intent intent = new Intent();
//            intent.setAction("com.android.settings.TTS_SETTINGS");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            return true;
//        }
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