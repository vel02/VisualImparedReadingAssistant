package sti.software.engineering.reading.assistant.ui.home.sub.read;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.adapter.ImageRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.FragmentHomeBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.ProcessDatabaseDataManager;
import sti.software.engineering.reading.assistant.util.SaveButtonStateHelper;
import sti.software.engineering.reading.assistant.util.StoreCroppedImageManager;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_AUTO_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_GALLERY_CODE;
import static sti.software.engineering.reading.assistant.ui.home.sub.read.HomeFragmentViewModel.SelectImageFrom.AUTO_CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.sub.read.HomeFragmentViewModel.SelectImageFrom.CAMERA;
import static sti.software.engineering.reading.assistant.ui.home.sub.read.HomeFragmentViewModel.SelectImageFrom.GALLERY;


public class HomeFragment extends DaggerFragment implements HomeActivity.OnStartThroughServiceListener {

    private static final String TAG = "HomeFragment";

    public void onImageClicked(Image image, Uri uri) {
        binding.imvViewImage.setImageURI(uri);
        viewModel.setExtractText(true);
    }

    @Override
    public void onStartedFromService() {
        viewModel.setSelectImageFrom(AUTO_CAMERA);
    }

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentHomeBinding binding;
    private HomeFragmentViewModel viewModel;
    private SelectImageFrom selectImageFrom;

    private Uri imageUri;
    private File capturedImage;
    private String filename;

    private ImageRecyclerAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), providerFactory).get(HomeFragmentViewModel.class);
        ((HomeActivity) requireActivity()).setOnStartThroughServiceListener(this);
        subscribeObservers();
        initImageRecyclerAdapter();
        navigate();
    }


    private void initImageRecyclerAdapter() {
        binding.viewList.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
        adapter = new ImageRecyclerAdapter();
        binding.viewList.setAdapter(adapter);
    }

    private void navigate() {
        binding.btnSave.setOnClickListener(v -> {
            if (filename != null) {
                Log.d(TAG, "save: called");
                Image image = new Image("nickname", filename);
                viewModel.insert(image);
                ProcessDatabaseDataManager.refresh(viewModel, 1000, 1000).start();
                SaveButtonStateHelper.getInstance().setSaveButtonState(requireActivity(), false);
                binding.btnSave.setEnabled(false);
            }
        });
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
                if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable)) return;
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

        viewModel.observedImages().removeObservers(getViewLifecycleOwner());
        viewModel.observedImages().observe(getViewLifecycleOwner(), images -> {
            if (images != null) {
                adapter.refresh(images);
                Log.d(TAG, "Images: " + images);
            }
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
    public void onResume() {
        super.onResume();
        viewModel.processDatabaseData();
        binding.btnSave.setEnabled(SaveButtonStateHelper.getInstance().getSaveButtonState(requireActivity()));
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

                binding.imvViewImage.setImageURI(imageUri);
                viewModel.setExtractText(true);
                SaveButtonStateHelper.getInstance().setSaveButtonState(requireActivity(), true);
            }
        }
    }

}