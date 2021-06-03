package sti.software.engineering.reading.assistant.ui.home.sub.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.FragmentCameraBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.OnHostPermissionListener;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.StoreCroppedImageManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.util.Utility.Files.FILE_EXTENSION_JPG;
import static sti.software.engineering.reading.assistant.util.Utility.Files.FILE_PATH_DOT;
import static sti.software.engineering.reading.assistant.util.Utility.Files.FILE_PATH_SEPARATOR_SLASH;
import static sti.software.engineering.reading.assistant.util.Utility.Files.getUriForFile;
import static sti.software.engineering.reading.assistant.util.Utility.Files.renameFile;
import static sti.software.engineering.reading.assistant.util.Utility.Messages.snackMessage;
import static sti.software.engineering.reading.assistant.util.Utility.Strings.CHAR_SEQUENCE_WHITESPACE;


public class CameraFragment extends DaggerFragment {

    private static final String TAG = "CameraFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentCameraBinding binding;

    private CameraFragmentViewModel viewModel;
    private SelectImageFrom selectImageFrom;
    private OnHostPermissionListener hostPermission;

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
                viewModel.setButtonSaveState(true);
                viewModel.setButtonEditState(true);
                Glide.with(this).load(uri)
                        .error(R.drawable.ic_capture_image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imageUri = null;
                                viewModel.setButtonSaveState(false);
                                viewModel.setButtonEditState(false);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(binding.imvCaptureImage);
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

        viewModel.setButtonSaveState(false);
        viewModel.observedButtonSaveState().removeObservers(getViewLifecycleOwner());
        viewModel.observedButtonSaveState().observe(getViewLifecycleOwner(), state -> {
            if (state != null) binding.btnSave.setEnabled(state);
        });

        viewModel.setButtonEditState(false);
        viewModel.observedButtonEditState().removeObservers(getViewLifecycleOwner());
        viewModel.observedButtonEditState().observe(getViewLifecycleOwner(), state -> {
            if (state != null) binding.btnEdit.setEnabled(state);
        });

    }

    private void navigate() {
        binding.imvCaptureImage.setOnClickListener(v -> {
            if (hostPermission.onCheckCameraPermission()) {
                hostPermission.onRequestCameraPermission();
                return;
            }

            if ((binding.imageView.getDrawable() != null) && (imageUri != null)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.dialog_title_retake);
                builder.setMessage(R.string.dialog_message_retake);
                builder.setPositiveButton(R.string.dialog_positive_yes, (dialog, which) -> this.selectImageFromCamera());
                builder.setNegativeButton(R.string.dialog_negative_no, (dialog, which) -> dialog.dismiss());
                builder.create().show();
            } else this.selectImageFromCamera();

        });

        binding.btnEdit.setOnClickListener(v -> {

            if (imageUri != null)
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireActivity(), this);

        });

        binding.btnSave.setOnClickListener(v -> {

            if (filename != null) {
                this.dialogSaveImage();
            }

        });
    }

    private void dialogSaveImage() {

        final View view = getLayoutInflater().inflate(R.layout.dialog_save_image, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.dialog_title_save_image);
        builder.setMessage(R.string.dialog_message_save_image);

        final EditText edt_filename = view.findViewById(R.id.edt_filename);
        String current_filename = filename.substring(0, filename.indexOf(FILE_PATH_DOT));
        edt_filename.setText(current_filename);

        builder.setPositiveButton(R.string.dialog_possitive_save, (dialog, which) -> {

            String preferred_filename = edt_filename.getText().toString().trim();
            if (preferred_filename.isEmpty() || preferred_filename.contains(FILE_PATH_DOT) || preferred_filename.contains(CHAR_SEQUENCE_WHITESPACE)) {
                snackMessage(binding.getRoot(), "Failed to save, entered filename is not valid",
                        getString(R.string.label_ok), Snackbar.LENGTH_INDEFINITE);
                return;
            }

            preferred_filename = preferred_filename + FILE_EXTENSION_JPG;
            File newFilename = new File(capturedImage.getParent() + FILE_PATH_SEPARATOR_SLASH + preferred_filename);

            Image image = new Image();
            if (!preferred_filename.equalsIgnoreCase(filename) && renameFile(capturedImage, newFilename)) {
                Log.i(TAG, "RENAMED");
                Uri uri = getUriForFile(requireContext(), newFilename);
                image.setFilename(preferred_filename);
                image.setUri(uri.toString());
                image.setFile(newFilename.toString());
            } else {
                Log.i(TAG, "NOT RENAMED");
                image.setFilename(filename);
                image.setUri(imageUri.toString());
                image.setFile(capturedImage.toString());
            }

            viewModel.insert(image);


            //reset
            binding.imvCaptureImage.setImageResource(R.drawable.ic_capture_image);
            viewModel.setButtonSaveState(false);
            viewModel.setButtonEditState(false);
            imageUri = null;
        });

        builder.setNegativeButton(R.string.dialog_negative_save, (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setView(view);
        builder.create().show();

    }

    public void selectImageFromCamera() {
        selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_CAMERA);
        startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
        imageUri = selectImageFrom.getImageUri();
        capturedImage = selectImageFrom.getFile();
        filename = selectImageFrom.getFilename();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof OnHostPermissionListener)) {
            assert activity != null;
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement OnHostPermissionListener interface.");
        }
        hostPermission = (OnHostPermissionListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostPermission = null;
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
            Log.d(TAG, "onActivityResult: " + capturedImage);
            viewModel.setImageUriLiveData(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "onActivityResult: from camera fragment called!");
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