package sti.software.engineering.reading.assistant.ui.about;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import sti.software.engineering.reading.assistant.BaseActivity;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ActivityAboutBinding;
import sti.software.engineering.reading.assistant.ui.Hostable;
import sti.software.engineering.reading.assistant.ui.about.sub.about.AboutFragmentDirections;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.InstructionsFragmentDirections;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionOneFragmentDirections;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionThreeFragmentDirections;
import sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub.InstructionTwoFragmentDirections;

public class AboutActivity extends BaseActivity implements Hostable {

    @Override
    public void onInflate(View view, String screen) {

        NavDirections directions;

        switch (screen) {
            case "tag_fragment_instruction":
                directions = AboutFragmentDirections.actionNavAboutToInstructionsFragment();
                Navigation.findNavController(view).navigate(directions);
                break;
            case "tag_fragment_contact_us":
                directions = AboutFragmentDirections.actionNavAboutToContactUsFragment();
                Navigation.findNavController(view).navigate(directions);
                break;

            case "tag_fragment_instruction_one":
                directions = InstructionsFragmentDirections.actionNavInstructionsToInstructionOneFragment();
                Navigation.findNavController(view).navigate(directions);
                break;

            case "tag_fragment_instruction_two":
                directions = InstructionOneFragmentDirections.actionNavInstructionOneToInstructionTwoFragment();
                Navigation.findNavController(view).navigate(directions);
                break;

            case "tag_fragment_instruction_three":
                directions = InstructionTwoFragmentDirections.actionNavInstructionTwoToInstructionThreeFragment();
                Navigation.findNavController(view).navigate(directions);
                break;

            case "tag_fragment_instruction_four":
                directions = InstructionThreeFragmentDirections.actionNavInstructionThreeToInstructionFourFragment();
                Navigation.findNavController(view).navigate(directions);
                break;

        }


    }

    private ActivityAboutBinding binding;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        navigationController();
    }

    private void navigationController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder().build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!(navController.navigateUp() || super.onSupportNavigateUp())) {
            onBackPressed();
        }
        return true;
    }

}