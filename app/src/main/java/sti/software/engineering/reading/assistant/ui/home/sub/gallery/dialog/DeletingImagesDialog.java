package sti.software.engineering.reading.assistant.ui.home.sub.gallery.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerDialogFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.DialogDeletingImagesBinding;

public class DeletingImagesDialog extends DaggerDialogFragment {

    @Override
    public int getTheme() {
        return R.style.RoundedCornersDialog;
    }

    private DialogDeletingImagesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = DialogDeletingImagesBinding.inflate(inflater);
        setCancelable(false);
        return this.binding.getRoot();
    }
}
