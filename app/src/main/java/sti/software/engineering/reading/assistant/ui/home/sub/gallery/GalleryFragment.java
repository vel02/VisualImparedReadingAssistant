package sti.software.engineering.reading.assistant.ui.home.sub.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.adapter.gallery.GalleryRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.FragmentGalleryBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.OnNotifyHostListener;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.dialog.DeletingImagesDialog;
import sti.software.engineering.reading.assistant.ui.picture.PictureActivity;
import sti.software.engineering.reading.assistant.util.ProcessDatabaseDataManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static sti.software.engineering.reading.assistant.util.Utility.IntentKeys.INTENT_KEY_PHOTO_CLICKED;

public class GalleryFragment extends DaggerFragment implements GalleryRecyclerAdapter.OnImageClickListener {

    private static final String TAG = "GalleryFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentGalleryBinding binding;
    private GalleryRecyclerAdapter adapter;

    private GalleryFragmentViewModel viewModel;

    private List<Image> selectedImages = new ArrayList<>();
    private boolean isImageMarked;

    private DeletingImagesDialog dialog;
    private OnNotifyHostListener notify;

    @Override
    public void onImageClicked(Image image) {
        Log.d(TAG, "onImageClicked: " + image.toString());
        Intent intent = new Intent(requireContext(), PictureActivity.class);
        intent.putExtra(INTENT_KEY_PHOTO_CLICKED, image);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onImageHighlighted(List<Image> selectedItems) {
        Log.d(TAG, "onImageHighlighted: " + selectedItems.size());
        this.isImageMarked = true;
        this.selectedImages = selectedItems;
        this.setButtonGalleryRemoveVisibility(selectedItems);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentGalleryBinding.inflate(inflater);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(requireActivity(), this.providerFactory).get(GalleryFragmentViewModel.class);
        initGalleryRecyclerAdapter();
        navigate();
        subscribeObservers();
    }

    private void navigate() {
        this.binding.btnGalleryRemove.setOnClickListener(v -> {
            this.dialog = new DeletingImagesDialog();
            this.dialog.show(requireActivity().getSupportFragmentManager(), "dialog_deleting_images");
            Log.d(TAG, "navigate: selected are " + this.selectedImages.toString());
            for (Image image : this.selectedImages) this.viewModel.removeImage(image);
            ProcessDatabaseDataManager.refresh(this.viewModel, 1000, 100).start();
        });
    }

    private void initGalleryRecyclerAdapter() {
        this.binding.viewList.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        this.adapter = new GalleryRecyclerAdapter();
        this.adapter.setOnImageClickListener(this);
        this.binding.viewList.setAdapter(this.adapter);
    }

    private void subscribeObservers() {
        this.viewModel.observedImages().removeObservers(getViewLifecycleOwner());
        this.viewModel.observedImages().observe(getViewLifecycleOwner(), images -> {
            if (images != null) {
                Log.d(TAG, "subscribeObservers: " + images);
                this.adapter.refresh(images);
                if (images.size() == 0) this.binding.placeholderGallery.setVisibility(View.VISIBLE);
                else this.binding.placeholderGallery.setVisibility(View.GONE);
                this.resetButtonGalleryRemoveUI();
            }
        });
    }

    private void resetButtonGalleryRemoveUI() {
        if (this.isImageMarked) {
            this.binding.viewList.setAdapter(this.adapter);
            this.adapter.resetSelectedItem();
            this.binding.btnGalleryRemove.setVisibility(View.GONE);
            this.isImageMarked = false;
            this.notify.onGalleryDeletingImages();
            if (this.dialog != null) this.dialog.dismiss();
        }
    }

    private void setButtonGalleryRemoveVisibility(List<Image> selectedItems) {
        if (selectedItems.size() > 0) {
            this.binding.btnGalleryRemove.setVisibility(View.VISIBLE);
        } else this.binding.btnGalleryRemove.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof OnNotifyHostListener)) {
            assert activity != null;
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement OnNotifyHostListener interface.");
        }
        notify = (OnNotifyHostListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notify = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viewModel.processDatabaseData();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.viewModel.processDatabaseData();
    }
}