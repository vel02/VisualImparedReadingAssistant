package sti.software.engineering.reading.assistant.ui.home.sub.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.databinding.FragmentGalleryBinding;

public class GalleryFragment extends DaggerFragment {

    private FragmentGalleryBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater);
        return binding.getRoot();
    }
}