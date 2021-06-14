package sti.software.engineering.reading.assistant.ui.about.sub;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.databinding.FragmentContactUsBinding;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ContactUsFragment extends DaggerFragment {

    private FragmentContactUsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactUsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDescriptionOne.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            binding.tvDescriptionTwo.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            binding.tvDescriptionThree.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}