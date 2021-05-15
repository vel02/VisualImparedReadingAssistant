package sti.software.engineering.reading.assistant.ui.home.sub.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_CAMERA_CODE;


public class CameraFragment extends DaggerFragment {

    private static final String TAG = "CameraFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentCameraBinding binding;

    private CameraFragmentViewModel viewModel;
    private SelectImageFrom selectImageFrom;
    private OnHostPermissionListener hostPermission;
    private Activity activity;

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

        viewModel.observedShowPermissionRational().removeObservers(getViewLifecycleOwner());
        viewModel.observedShowPermissionRational().observe(getViewLifecycleOwner(), showPermissionRational -> {
            if (showPermissionRational) {
                Snackbar.make(binding.getRoot(), R.string.label_permission_rational, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.label_grant_permission, v -> {
                            Intent permissionIntent = new Intent();
                            permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    requireActivity().getPackageName(), null);
                            permissionIntent.setData(uri);
                            permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(permissionIntent);
                        }).show();
            }
        });
    }

    private void navigate() {
        binding.imvCaptureImage.setOnClickListener(v -> {
            if (this.checkCameraPermission()) {
                hostPermission.onRequestCameraPermission();
                return;
            }
            this.selectImageFromCamera();
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
        activity = getActivity();
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            if (grantResults.length > 0) {
//                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                Log.d(TAG, "onRequestPermissionsResult: camera should work!" + (cameraAccepted == storageAccepted));
//                if (cameraAccepted && storageAccepted) {
//                    selectImageFromCamera();
//                } else viewModel.setShowPermissionRational(true);
//            }
//        }
//    }

    private boolean checkCameraPermission() {
        return (PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                || PackageManager.PERMISSION_DENIED == ActivityCompat
                .checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE));
    }

//    private void requestCameraPermission() {
//        boolean shouldProvideRational = (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
//                || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE));
//
//        if (shouldProvideRational) {
//            Snackbar.make(binding.getRoot(),
//                    R.string.label_permission_rational, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.label_ok, v ->
//                            requestPermissions(new String[]{
//                                            Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE},
//                                    CAMERA_REQUEST_CODE)).show();
//        } else requestPermissions(new String[]{
//                        Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE},
//                CAMERA_REQUEST_CODE);
//    }

    private void resetImageCapture() {
        if (binding.imvCaptureImage.getDrawable() == null) {
            binding.imvCaptureImage.setImageResource(R.drawable.ic_capture_image);
        }
    }
}