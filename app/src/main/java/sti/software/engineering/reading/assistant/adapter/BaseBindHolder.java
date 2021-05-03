package sti.software.engineering.reading.assistant.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sti.software.engineering.reading.assistant.model.Image;

public abstract class BaseBindHolder extends RecyclerView.ViewHolder {

    private Image image;

    public BaseBindHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected abstract void clear();

    protected abstract void initialization();

    public void onBind(Image image) {
        this.image = image;
        this.initialization();
        this.clear();
    }

    public Image getImage() {
        return image;
    }
}
