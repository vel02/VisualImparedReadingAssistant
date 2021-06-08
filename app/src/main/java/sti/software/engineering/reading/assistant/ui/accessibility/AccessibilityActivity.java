package sti.software.engineering.reading.assistant.ui.accessibility;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityAccessibilityBinding;


public class AccessibilityActivity extends BaseActivity implements Hostable {

    @Override
    public void onInflate(View view, String screen) {

    }

    private ActivityAccessibilityBinding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AccessibilityActivity.this, R.layout.activity_accessibility);
        navigationController();
    }

    private void navigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }


}