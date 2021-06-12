package sti.software.engineering.reading.assistant.ui.picture.sub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.FragmentPictureBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.picture.PictureViewModel;
import sti.software.engineering.reading.assistant.util.StoreCroppedImageManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.util.Utility.ArgKeys.BUNDLE_KEY_PHOTO_CLICKED;
import static sti.software.engineering.reading.assistant.util.Utility.Files.FILE_PATH_DOT;


public class PictureFragment extends DaggerFragment {

    private static final String TAG = "PictureFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentPictureBinding binding;
    private PictureViewModel viewModel;

    private String filename;
    private String fileLocation;

    private Image image;
    private Uri uri;
    private File file;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.retrieveStartDestinationArgs();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPictureBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.viewModel = new ViewModelProvider(this, providerFactory).get(PictureViewModel.class);
        this.subscribeObservers();
        this.binding.setFilename(this.filename);
        this.binding.setFile("Location: " + this.fileLocation);
        this.binding.setUri(this.uri);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(this.filename);
    }

    private void subscribeObservers() {
        viewModel.observedImageUri().removeObservers(getViewLifecycleOwner());
        viewModel.observedImageUri().observe(getViewLifecycleOwner(), imageUri -> {
            if (imageUri != null) {
                Glide.with(this).load(imageUri)
                        .error(R.drawable.ic_capture_image)
                        .into(binding.imageItemPlaceholder);
            }
        });

        viewModel.observedStoreCroppedImage().removeObservers(getViewLifecycleOwner());
        viewModel.observedStoreCroppedImage().observe(getViewLifecycleOwner(), imageUri -> {
            if (imageUri != null) {
                File capturedImageFile = this.file;
                if (capturedImageFile == null) return;
                StoreCroppedImageManager.storeImage(requireActivity(), capturedImageFile, imageUri, filename + ".jpg");
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_image, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if (uri != null)
                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireActivity(), this);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            this.dialogDeleteImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogDeleteImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.dialog_delete_image_title);
        builder.setMessage(R.string.dialog_delete_image_message);
        builder.setPositiveButton(R.string.dialog_positive_yes, (dialog, which) -> {
            viewModel.removeImage(this.image);
            requireActivity().finish();
        });
        builder.setNegativeButton(R.string.dialog_negative_no, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private String removeFileExtension(String filename) {
        return filename.substring(0, filename.lastIndexOf(FILE_PATH_DOT));
    }

    private void retrieveStartDestinationArgs() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Image image = bundle.getParcelable(BUNDLE_KEY_PHOTO_CLICKED);
            if (image != null) {
                this.image = image;
                this.filename = removeFileExtension(image.getFilename());
                this.fileLocation = image.getFile();
                this.uri = Uri.parse(image.getUri());
                this.file = image.getFileObject();
            }
        }
    }
}