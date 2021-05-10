package sti.software.engineering.reading.assistant.ui.home.sub.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.databinding.FragmentCameraBinding;


public class CameraFragment extends DaggerFragment {

    private FragmentCameraBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater);
        return binding.getRoot();
    }
}