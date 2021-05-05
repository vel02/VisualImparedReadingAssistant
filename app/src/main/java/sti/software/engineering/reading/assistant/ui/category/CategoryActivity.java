package sti.software.engineering.reading.assistant.ui.category;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import dagger.android.support.DaggerAppCompatActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityCategoryBinding;

public class CategoryActivity extends DaggerAppCompatActivity {

    private ActivityCategoryBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        initialize();
    }

    private void initialize() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}