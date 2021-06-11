package sti.software.engineering.reading.assistant.adapter.gallery;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.GalleryItemBinding;
import sti.software.engineering.reading.assistant.model.Image;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryBindHolder> {

    private List<Image> images = new ArrayList<>();
    private final List<Image> selectedItems = new ArrayList<>();
    private boolean isSelectedMode;

    public void refresh(List<Image> images) {
        Collections.reverse(images);
        this.images = images;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GalleryBindHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GalleryItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.gallery_item,
                        parent, false);
        return new GalleryBindHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryBindHolder holder, int position) {
        holder.onBind(images.get(position));
        int highlight = holder.itemView.getResources().getColor(R.color.secondaryColor);
        holder.itemView.setOnLongClickListener(v -> {
            isSelectedMode = true;
            Image image = images.get(position);

            if (selectedItems.contains(image)) {
                image.setSelected(false);

                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                selectedItems.remove(image);
            } else {
                image.setSelected(true);

                holder.itemView.setBackgroundColor(highlight);
                selectedItems.add(image);
            }

            listener.onImageSelected(selectedItems);

            if (selectedItems.size() == 0) {
                isSelectedMode = false;
            }
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            Image image = images.get(position);

            if (isSelectedMode) {

                if (selectedItems.contains(image)) {
                    image.setSelected(false);

                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    selectedItems.remove(image);
                } else {
                    image.setSelected(true);

                    holder.itemView.setBackgroundColor(highlight);
                    selectedItems.add(image);
                }

                listener.onImageSelected(selectedItems);

                if (selectedItems.size() == 0) {
                    isSelectedMode = false;
                }

            } else {
                TypedValue outValue = new TypedValue();
                holder.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                holder.itemView.setBackgroundResource(outValue.resourceId);
                listener.onImageClicked(image);
            }
        });
    }

    public void resetSelectedItem() {
        isSelectedMode = false;
        selectedItems.clear();
    }

    @Override
    public int getItemCount() {
        return ((images != null && images.size() > 0) ? images.size() : 0);
    }

    public interface OnImageClickListener {
        void onImageClicked(Image image);

        void onImageSelected(List<Image> selectedItems);
    }

    private OnImageClickListener listener;

    public void setOnImageClickListener(final OnImageClickListener listener) {
        this.listener = listener;
    }

}
