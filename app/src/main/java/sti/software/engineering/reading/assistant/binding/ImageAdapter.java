package sti.software.engineering.reading.assistant.binding;

import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import sti.software.engineering.reading.assistant.R;

public class ImageAdapter {

    @BindingAdapter({"sti:placeholder"})
    public static void setPlaceholder(ImageView view, Uri uri) {
        if (uri != null) {
            Picasso.get()
                    .load(uri)
                    .centerCrop()
                    .fit()
                    .into(view);
        } else view.setImageResource(R.drawable.image_placeholder);
    }

}
