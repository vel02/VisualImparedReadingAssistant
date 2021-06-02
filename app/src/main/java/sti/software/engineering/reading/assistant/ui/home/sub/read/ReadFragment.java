package sti.software.engineering.reading.assistant.ui.home.sub.read;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.adapter.ImageRecyclerAdapter;
import sti.software.engineering.reading.assistant.databinding.FragmentReadBinding;
import sti.software.engineering.reading.assistant.model.Image;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity;
import sti.software.engineering.reading.assistant.ui.home.HomeActivity.OnStartThroughServiceListener;
import sti.software.engineering.reading.assistant.ui.home.selection.SelectImageFrom;
import sti.software.engineering.reading.assistant.util.TextToSpeechHelper;
import sti.software.engineering.reading.assistant.viewmodel.ViewModelProviderFactory;

import static android.app.Activity.RESULT_OK;
import static sti.software.engineering.reading.assistant.BaseActivity.IMAGE_PICK_AUTO_CAMERA_CODE;
import static sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragmentViewModel.SelectImageFrom.AUTO_CAMERA;


public class ReadFragment extends DaggerFragment implements OnStartThroughServiceListener {

    private static final String TAG = "HomeFragment";

    public void onImageClicked(Image image, Uri uri) {
        Glide.with(this).load(uri).into(binding.imvViewImage);
    }

    @Override
    public void onStartedFromService() {
        viewModel.setSelectImageFrom(AUTO_CAMERA);
    }

    @Inject
    ViewModelProviderFactory providerFactory;

    private FragmentReadBinding binding;
    private ReadFragmentViewModel viewModel;
    private SelectImageFrom selectImageFrom;

    private Uri imageUri;
//    private File capturedImage;
//    private String filename;

    private ImageRecyclerAdapter adapter;
    private TextToSpeechHelper textToSpeech;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity(), providerFactory).get(ReadFragmentViewModel.class);
        ((HomeActivity) requireActivity()).setOnStartThroughServiceListener(this);
        textToSpeech = new TextToSpeechHelper(requireContext());//TextToSpeechHelper.getInstance(requireContext());
        navigate();
        subscribeObservers();
        initImageRecyclerAdapter();
    }

    private void navigate() {
        binding.imvViewImage.setOnClickListener(v -> {
            if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable)) return;
            viewModel.setExtractText(true);
        });

        binding.btnRead.setOnClickListener(v -> {
            if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable)) return;
            viewModel.setExtractText(true);
        });

        binding.btnStop.setOnClickListener(v -> {
            //Temporary location open TTS settings using intent
            //https://stackoverflow.com/questions/3160447/how-to-show-up-the-settings-for-text-to-speech-in-my-app
//            Intent intent = new Intent();
//            intent.setAction("com.android.settings.TTS_SETTINGS");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
        });
    }

    private void initImageRecyclerAdapter() {
        binding.viewList.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false));
        adapter = new ImageRecyclerAdapter();
        binding.viewList.setAdapter(adapter);
    }

    private void subscribeObservers() {
        viewModel.observedSelectedImage().removeObservers(getViewLifecycleOwner());
        viewModel.observedSelectedImage().observe(getViewLifecycleOwner(), selectImage -> {
            if (selectImage != null) {
                if (selectImage == AUTO_CAMERA) {
                    selectImageFrom = new SelectImageFrom(requireActivity(), SelectImageFrom.SELECT_CAMERA);
                    startActivityForResult(selectImageFrom.pickCamera(), IMAGE_PICK_AUTO_CAMERA_CODE);
                    imageUri = selectImageFrom.getImageUri();
//                        capturedImage = selectImageFrom.getFile();
//                        filename = selectImageFrom.getFilename();
                }
            }
        });

        viewModel.setExtractText(false);
        viewModel.observedExtractText().removeObservers(getViewLifecycleOwner());
        viewModel.observedExtractText().observe(getViewLifecycleOwner(), extract -> {
            if (extract) {
                new Thread(() -> {
                    if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable)) {
                        Log.d(TAG, "subscribeObservers: imvViewImage = " + binding.imvViewImage.getDrawable());
                        return;
                    }

                    BitmapDrawable drawable = (BitmapDrawable) binding.imvViewImage.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    TextRecognizer recognizer = new TextRecognizer
                            .Builder(requireActivity()).build();
                    if (!recognizer.isOperational()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireActivity(), "Cannot recognize text", Toast.LENGTH_SHORT).show());
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
                        textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH);
                    }
                }).start();
            }
        });

        viewModel.observedImages().removeObservers(getViewLifecycleOwner());
        viewModel.observedImages().observe(getViewLifecycleOwner(), images -> {
            if (images != null) {
                adapter.refresh(images);
                Log.d(TAG, "Images: " + images);
            }
        });

    }

    public void receiveCroppedImage(Uri cropped) {
        //save cropped image to app folder, replacing the initial image.
        //should be on the background/thread
        viewModel.storeCroppedImage(cropped);
        Glide.with(this).load(cropped).into(binding.imvViewImage);
        viewModel.setExtractText(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.processDatabaseData();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_AUTO_CAMERA_CODE) {
                Glide.with(this).load(imageUri).into(binding.imvViewImage);
            }
        }
    }

}