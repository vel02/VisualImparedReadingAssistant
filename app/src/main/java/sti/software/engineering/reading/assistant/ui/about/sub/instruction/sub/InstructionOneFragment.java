package sti.software.engineering.reading.assistant.ui.about.sub.instruction.sub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.FragmentInstructionOneBinding;
import sti.software.engineering.reading.assistant.ui.Hostable;

public class InstructionOneFragment extends DaggerFragment {

    private FragmentInstructionOneBinding binding;
    private Hostable hostable;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInstructionOneBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.zoomView.setImageResource(R.drawable.im_instruction_two);
        navigate();
    }

    private void navigate() {
        binding.btnNext.setOnClickListener(v -> {
            hostable.onInflate(v, getString(R.string.tag_fragment_instruction_two));
        });
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof Hostable)) {
            assert activity != null;
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Hostable interface.");
        }
        hostable = (Hostable) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostable = null;
    }
}