package sti.software.engineering.reading.assistant.ui.home;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import dagger.android.support.DaggerAppCompatActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityHomeBinding;

public class HomeActivity extends DaggerAppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(HomeActivity.this, R.layout.activity_home);
    }

}