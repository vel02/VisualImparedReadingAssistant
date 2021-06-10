package sti.software.engineering.reading.assistant.ui.about.sub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.databinding.FragmentInstructionsBinding;


public class InstructionsFragment extends DaggerFragment {

    private FragmentInstructionsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstructionsBinding.inflate(inflater);
        return binding.getRoot();
    }
}