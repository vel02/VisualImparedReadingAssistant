package sti.software.engineering.reading.assistant.binding;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import sti.software.engineering.reading.assistant.R;

public class ImageAdapter {

    @BindingAdapter({"sti:placeholder"})
    public static void setPlaceholder(ImageView view, Uri uri) {
        if (uri != null) {
            Glide.with(view.getContext()).load(uri).into(view);
        } else view.setImageResource(R.drawable.image_placeholder);
    }

}
