package sti.software.engineering.reading.assistant.adapter.gallery;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.adapter.BaseBindHolder;
import sti.software.engineering.reading.assistant.databinding.GalleryItemBinding;
import sti.software.engineering.reading.assistant.model.Image;

public class GalleryBindHolder extends BaseBindHolder {

    private final GalleryItemBinding binding;

    public GalleryBindHolder(@NonNull View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    @Override
    protected void clear() {
        binding.imageItemPlaceholder.setImageResource(R.drawable.image_placeholder);
    }

    @Override
    protected void initialization() {
    }

    @Override
    public void onBind(Image image) {
        super.onBind(image);
        Uri uri = FileProvider.getUriForFile(itemView.getContext(),
                itemView.getContext().getPackageName()
                        + ".provider", image.getFileObject());
        binding.setImage(image);
        binding.setUri(uri);
    }
}
