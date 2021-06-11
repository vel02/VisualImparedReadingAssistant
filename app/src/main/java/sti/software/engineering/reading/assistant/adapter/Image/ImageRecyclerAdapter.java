package sti.software.engineering.reading.assistant.adapter.Image;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.ImageItemBinding;
import sti.software.engineering.reading.assistant.model.Image;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageBindHolder> {

    private List<Image> images = new ArrayList<>();

    public void refresh(List<Image> images) {
        Collections.reverse(images);
        this.images = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageBindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.image_item,
                        parent, false);
        return new ImageBindHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ImageBindHolder holder, int position) {
        holder.onBind(images.get(position));
    }

    @Override
    public int getItemCount() {
        return ((images != null && images.size() > 0) ? images.size() : 0);
    }

    public interface OnImageClickListener {
        void onImageClicked(Image image, Uri uri);
    }

}
