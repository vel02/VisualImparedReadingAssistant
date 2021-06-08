package sti.software.engineering.reading.assistant.ui.home.sub.read;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
import static sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragmentViewModel.UtteranceProgress.UTTERANCE_DONE_READING;
import static sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragmentViewModel.UtteranceProgress.UTTERANCE_START_READING;
import static sti.software.engineering.reading.assistant.util.TextToSpeechHelper.UTTERANCE_ID_READING_TEXT;
import static sti.software.engineering.reading.assistant.util.Utility.Messages.toastMessage;


public class ReadFragment extends DaggerFragment implements
        OnStartThroughServiceListener//  HomeActivity.OnVoiceChangeListener
{

    private static final String TAG = "HomeFragment";

//    @Override
//    public void onVoiceChanged() {
//        textToSpeech = new TextToSpeechHelper(requireContext());
//        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
//    }

    private final TextToSpeechHelper.OnUtteranceProgressListener utteranceProgressListener = new TextToSpeechHelper.OnUtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            if (utteranceId.equalsIgnoreCase(UTTERANCE_ID_READING_TEXT)) {
                viewModel.postUtteranceProgress(UTTERANCE_START_READING);
                viewModel.postButtonStopState(true);
                viewModel.postButtonReadState(false);
            }
        }

        @Override
        public void onDone(String utteranceId) {
            if (utteranceId.equalsIgnoreCase(UTTERANCE_ID_READING_TEXT)) {
                viewModel.postUtteranceProgress(UTTERANCE_DONE_READING);
                viewModel.postButtonStopState(false);
                viewModel.postButtonReadState(true);
            }
        }

        @Override
        public void onError(String utteranceId) {
            if (utteranceId.equalsIgnoreCase(UTTERANCE_ID_READING_TEXT)) {
                viewModel.postButtonStopState(false);
            }
        }
    };

    public void onImageClicked(Image image, Uri uri) {
        Glide.with(this).load(uri).into(binding.imvViewImage);
        viewModel.setButtonReadState(true);
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
    private boolean isReading;

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
//        ((HomeActivity) requireActivity()).setOnVoiceChangeListener(this);
        navigate();
        subscribeObservers();
        initImageRecyclerAdapter();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void navigate() {

        final GestureDetector gestureDetector = new GestureDetector(requireContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable))
                    return true;
                if (!isReading) viewModel.setExtractText(true);
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable))
                    return true;
                textToSpeech.stop();
                viewModel.setButtonStopState(false);
                viewModel.setButtonReadState(true);
                viewModel.setUtteranceProgress(UTTERANCE_DONE_READING);
                return true;
            }
        });

        binding.imvViewImage.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        binding.btnRead.setOnClickListener(v -> {
            if (!(binding.imvViewImage.getDrawable() instanceof BitmapDrawable)) return;
            viewModel.setExtractText(true);
        });

        binding.btnStop.setOnClickListener(v -> {
            textToSpeech.stop();
            viewModel.setButtonStopState(false);
            viewModel.setButtonReadState(true);
            viewModel.setUtteranceProgress(UTTERANCE_DONE_READING);


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
                                toastMessage(requireActivity(), "Cannot recognize text"));
                    } else {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < items.size(); i++) {
                            TextBlock item = items.valueAt(i);
                            sb.append(item.getValue());
                            sb.append(" ");
//                            sb.append("\n");
                        }

                        String text = sb.toString().replaceAll("\n", " ");
                        Log.d(TAG, "TEXT: " + text);
                        textToSpeech.speak(text, UTTERANCE_ID_READING_TEXT, TextToSpeech.QUEUE_FLUSH);
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

        viewModel.setButtonReadState(false);
        viewModel.observedButtonReadState().removeObservers(getViewLifecycleOwner());
        viewModel.observedButtonReadState().observe(getViewLifecycleOwner(), enable -> {
            if (enable != null) {
                binding.btnRead.setEnabled(enable);
            }
        });

        viewModel.setButtonStopState(false);
        viewModel.observedButtonStopState().removeObservers(getViewLifecycleOwner());
        viewModel.observedButtonStopState().observe(getViewLifecycleOwner(), enable -> {
            if (enable != null) {
                binding.btnStop.setEnabled(enable);
            }
        });

        viewModel.observedUtteranceProgress().removeObservers(getViewLifecycleOwner());
        viewModel.observedUtteranceProgress().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null) {
                switch (progress) {
                    case UTTERANCE_START_READING:
                        this.isReading = true;
                        break;
                    case UTTERANCE_DONE_READING:
                        this.isReading = false;
                        break;
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        textToSpeech = new TextToSpeechHelper(requireContext());
        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);

        viewModel.processDatabaseData();
    }

    @Override
    public void onStop() {
        if (textToSpeech != null) {
            textToSpeech.destroy();
        }

        //update stop ui when onStop() was called while AI is reading.
        viewModel.setUtteranceProgress(UTTERANCE_DONE_READING);
        viewModel.setButtonStopState(false);
        viewModel.setButtonReadState(true);
        super.onStop();
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
                viewModel.setButtonReadState(true);
            }
        }
    }

}