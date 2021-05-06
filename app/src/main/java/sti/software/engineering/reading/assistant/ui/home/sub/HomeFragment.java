package sti.software.engineering.reading.assistant.ui.home.sub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.databinding.FragmentHomeBinding;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.SaveButtonStateHelper;
import sti.software.engineering.reading.assistant.util.StoreCroppedImageManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_AUTO_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_GALLERY_CODE;
import static sti.software.engineering.reading.assistant.ui.home.sub.HomeFragmentViewModel.SelectImageFrom.CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.sub.HomeFragmentViewModel.SelectImageFrom.GALLERY;


public class HomeFragment extends DaggerFragment {

    private static final String TAG = "HomeFragment";

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentHomeBinding binding;
    private HomeFragmentViewModel viewModel;

    private SelectImageFrom selectImageFrom;

    private Uri imageUri;
    private File capturedImage;
    private String filename;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), providerFactory).get(HomeFragmentViewModel.class);
        subscribeObservers();
    }

    private void subscribeObservers() {
        viewModel.observedSelectedImage().removeObservers(getViewLifecycleOwner());
        viewModel.observedSelectedImage().observe(getViewLifecycleOwner(), selectImage -> {
            if (selectImage != null) {
                switch (selectImage) {
                    case AUTO_CAMERA:
                        selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_CAMERA);
                        startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_AUTO_CAMERA_CODE);
                        imageUri = selectImageFrom.getImageUri();
                        capturedImage = selectImageFrom.getFile();
                        filename = selectImageFrom.getFilename();
                        break;

                    case CAMERA:
                        selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_CAMERA);
                        startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_CAMERA_CODE);
                        imageUri = selectImageFrom.getImageUri();
                        capturedImage = selectImageFrom.getFile();
                        filename = selectImageFrom.getFilename();
                        break;

                    case GALLERY:
                        selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_GALLERY);
                        startActivityForResult(selectImageFrom.pickGallery(), IMAGE_PICK_GALLERY_CODE);
                        break;
                }
            }
        });

        viewModel.observedStoreCroppedImage().removeObservers(getViewLifecycleOwner());
        viewModel.observedStoreCroppedImage().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                File capturedImageFile = this.capturedImage;
                if (capturedImageFile == null) return;
                StoreCroppedImageManager.storeImage(requireActivity(), capturedImageFile, uri, filename);
            }
        });

        viewModel.observedExtractText().removeObservers(getViewLifecycleOwner());
        viewModel.observedExtractText().observe(getViewLifecycleOwner(), extract -> {
            new Thread(() -> {
                BitmapDrawable drawable = (BitmapDrawable) binding.imvViewImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer
                        .Builder(requireActivity()).build();
                if (!recognizer.isOperational()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireActivity(), "Cannot recognize text", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < items.size(); i++) {
                        TextBlock item = items.valueAt(i);
                        sb.append(item.getValue());
                        sb.append("\n");
                    }

                    Log.d(TAG, "result below:\n" + sb.toString());
                }
            }).start();
        });

    }


    public void selectImageFromCamera() {
        viewModel.setSelectImageFrom(CAMERA);
    }

    public void selectImageFromGallery() {
        viewModel.setSelectImageFrom(GALLERY);
    }

    public void receiveCroppedImage(Uri cropped) {
        //save cropped image to app folder, replacing the initial image.
        //should be on the background/thread
        viewModel.storeCroppedImage(cropped);
        binding.imvViewImage.setImageURI(cropped);
        viewModel.setExtractText(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                assert data != null;
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireActivity());
                SaveButtonStateHelper.getInstance().setSaveButtonState(requireActivity(), false);
            }

            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(requireActivity());
                SaveButtonStateHelper.getInstance().setSaveButtonState(requireActivity(), true);
            }

            if (requestCode == IMAGE_PICK_AUTO_CAMERA_CODE) {
                viewModel.storeCroppedImage(imageUri);

//                binding.imvViewImage.setImageURI(imageUri);
//                viewModel.setExtractText(true);
                SaveButtonStateHelper.getInstance().setSaveButtonState(requireActivity(), true);
            }
        }
    }
}