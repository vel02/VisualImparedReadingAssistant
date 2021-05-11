package sti.software.engineering.reading.assistant.ui.home.sub.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.FragmentCameraBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.StoreCroppedImageManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_CAMERA_CODE;


public class CameraFragment extends DaggerFragment {

    private static final String TAG = "CameraFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentCameraBinding binding;

    private CameraFragmentViewModel viewModel;
    private SelectImageFrom selectImageFrom;

    private Uri imageUri;
    private File capturedImage;
    private String filename;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), providerFactory).get(CameraFragmentViewModel.class);
        subscribeObservers();
        navigate();
    }

    private void subscribeObservers() {
        viewModel.observedImageUri().removeObservers(getViewLifecycleOwner());
        viewModel.observedImageUri().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                imageUri = uri;
                binding.imvCaptureImage.setImageURI(uri);
                return;
            }
            binding.imvCaptureImage.setImageResource(R.drawable.ic_capture_image);
        });

        viewModel.observedStoreCroppedImage().removeObservers(getViewLifecycleOwner());
        viewModel.observedStoreCroppedImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                File capturedImageFile = this.capturedImage;
                if (capturedImageFile == null) return;
                StoreCroppedImageManager.storeImage(requireActivity(), capturedImageFile, uri, filename);
            }
        });
    }

    private void navigate() {
        binding.imvCaptureImage.setOnClickListener(v -> {

            selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_CAMERA);
            startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
            imageUri = selectImageFrom.getImageUri();
            capturedImage = selectImageFrom.getFile();
            filename = selectImageFrom.getFilename();

        });

        binding.btnEdit.setOnClickListener(v -> {

            if (imageUri != null)
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireActivity(), this);

        });

        binding.btnSave.setOnClickListener(v -> {

            if (filename != null) {
                Image image = new Image();
                image.setFilename(filename);
                image.setNickname("nickname");
                viewModel.insert(image);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        resetImageCapture();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CAMERA_CODE) {
            Log.d(TAG, "onActivityResult: " + imageUri);
            viewModel.setImageUriLiveData(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri croppedImageUri = result.getUri();
                viewModel.storeCroppedImage(croppedImageUri);
                viewModel.setImageUriLiveData(croppedImageUri);
            }
        }
    }

    private void resetImageCapture() {
        if (binding.imvCaptureImage.getDrawable() == null) {
            binding.imvCaptureImage.setImageResource(R.drawable.ic_capture_image);
        }
    }
}