package sti.software.engineering.reading.assistant.ui.picture;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityPictureBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.service.TriggerCameraService;

import static sti.software.engineering.reading.assistant.util.Utility.ArgKeys.BUNDLE_KEY_PHOTO_CLICKED;
import static sti.software.engineering.reading.assistant.util.Utility.IntentKeys.INTENT_KEY_PHOTO_CLICKED;

public class PictureActivity extends BaseActivity {

    private static final String TAG = "PictureActivity";

    private ActivityPictureBinding binding;
    private NavController navController;

    private void receiveIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Image image = intent.getParcelableExtra(INTENT_KEY_PHOTO_CLICKED);
            if (image != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_KEY_PHOTO_CLICKED, image);
                this.navController.setGraph(R.navigation.nav_graph_picture, bundle);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_picture);
        this.navigationController();
        this.receiveIntent();
    }

    private void navigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        this.navController = navHostFragment.getNavController();
        Toolbar toolbar = this.binding.toolbar;
        setSupportActionBar(toolbar);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder().build();
        NavigationUI.setupActionBarWithNavController(this, this.navController, configuration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!(this.navController.navigateUp() || super.onSupportNavigateUp())) {
            onBackPressed();
        }
        return true;
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
}