package sti.software.engineering.reading.assistant.ui.accessibility.sub.accessibility;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.support.DaggerFragment;
import sti.software.engineering.reading.assistant.R;
import sti.software.engineering.reading.assistant.databinding.FragmentAccessibilityBinding;
import sti.software.engineering.reading.assistant.util.ApplicationSettings;
import sti.software.engineering.reading.assistant.util.TextToSpeechHelper;

import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_APPLICATION_VOICE_PITCH;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_APPLICATION_VOICE_PROGRESS_PITCH;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_APPLICATION_VOICE_PROGRESS_SPEED;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_APPLICATION_VOICE_SPEED;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_SYSTEM_VOICE_PITCH;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_SYSTEM_VOICE_PROGRESS_PITCH;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_SYSTEM_VOICE_PROGRESS_SPEED;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_SYSTEM_VOICE_SPEED;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_VOICE_APPLICATION;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.SETTINGS_VOICE_SYSTEM;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputApplicationPitchProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputApplicationSpeechRateProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputSystemPitchProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputSystemSpeechRateProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputApplicationPitchProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputApplicationPitchSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputApplicationSpeechRateProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputApplicationSpeechRateSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputSystemPitchProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputSystemPitchSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputSystemSpeechRateProgressSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputSystemSpeechRateSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.setInputVoiceSettings;

public class AccessibilityFragment extends DaggerFragment {

    private static final String TAG = "AccessibilityFragment";
    private FragmentAccessibilityBinding binding;

    private TextToSpeechHelper textToSpeech;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccessibilityBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textToSpeech = new TextToSpeechHelper(requireContext());

        if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(requireContext()))) {
            binding.fragmentAccessibilityVoiceApp.setChecked(true);
            displayApplicationSettings();
        } else {
            binding.fragmentAccessibilityVoiceDefault.setChecked(true);
            displaySystemSettings();
        }

        navigate();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void displaySystemSettings() {
        Log.d(TAG, "displaySystemSettings: called");
        binding.applicationSeekSpeed.setMax(100);
        binding.applicationSeekSpeed.setProgress(getOutputSystemSpeechRateProgressSettings(requireContext()));

        binding.applicationSeekPitch.setMax(100);
        binding.applicationSeekPitch.setProgress(getOutputSystemPitchProgressSettings(requireContext()));
    }

    private void displayApplicationSettings() {
        Log.d(TAG, "displayApplicationSettings: called");
        binding.applicationSeekSpeed.setMax(100);
        binding.applicationSeekSpeed.setProgress(getOutputApplicationSpeechRateProgressSettings(requireContext()));

        binding.applicationSeekPitch.setMax(100);
        binding.applicationSeekPitch.setProgress(getOutputApplicationPitchProgressSettings(requireContext()));
    }

    private void navigate() {
        binding.fragmentAccessibilityVoiceGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selected = group.getCheckedRadioButtonId();
            RadioButton button = binding.getRoot().findViewById(selected);

            destroyTextToSpeech();
            textToSpeech = new TextToSpeechHelper(requireContext());

            if (button.getTag().toString().equalsIgnoreCase(getString(R.string.label_system_language))) {
                setInputVoiceSettings(requireContext(), SETTINGS_VOICE_SYSTEM);
                displaySystemSettings();
            } else {
                setInputVoiceSettings(requireContext(), SETTINGS_VOICE_APPLICATION);
                displayApplicationSettings();
            }

            ApplicationSettings.setInputReInstantiateTTSSettings(requireContext(), ApplicationSettings.SETTINGS_INSTANTIATE_TTS_YES);

        });

        binding.applicationSeekSpeed.incrementProgressBy(1);
        binding.applicationSeekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed_progress = (float) progress / 50;
                Log.d(TAG, "onProgressChanged: " + speed_progress);
                if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(requireContext()))) {

                    float currentSpeed = 1.0F;
                    float difference;
                    //up
                    if (currentSpeed < speed_progress) {
                        difference = speed_progress - currentSpeed;
                        Log.d(TAG, speed_progress + " - " + currentSpeed + " = " + difference);
                        currentSpeed = currentSpeed - difference;
                        if (currentSpeed == 0.0) {
                            currentSpeed = 0.01F;
                        }
                    } else if (currentSpeed > speed_progress) {
                        difference = currentSpeed - speed_progress;
                        currentSpeed = currentSpeed + difference;
                    }

                    Log.d(TAG, "onProgressChanged: speed " + currentSpeed);
                    setInputApplicationSpeechRateSettings(requireContext(), currentSpeed);
                    setInputApplicationSpeechRateProgressSettings(requireContext(), progress);
                } else {
                    setInputSystemSpeechRateSettings(requireContext(), speed_progress);
                    setInputSystemSpeechRateProgressSettings(requireContext(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch: called");
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch: called");
                destroyTextToSpeech();
                textToSpeech = new TextToSpeechHelper(requireContext());

            }
        });

        binding.applicationSeekPitch.incrementProgressBy(1);
        binding.applicationSeekPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pitch_progress = (float) progress / 50;
                Log.d(TAG, "onProgressChanged: " + pitch_progress);
                if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(requireContext()))) {
                    setInputApplicationPitchSettings(requireContext(), pitch_progress);
                    setInputApplicationPitchProgressSettings(requireContext(), progress);
                } else {
                    setInputSystemPitchSettings(requireContext(), pitch_progress);
                    setInputSystemPitchProgressSettings(requireContext(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                destroyTextToSpeech();
                textToSpeech = new TextToSpeechHelper(requireContext());
            }
        });


        binding.accessibilityReset.setOnClickListener(v -> {


            if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(requireContext()))) {
                setInputApplicationSpeechRateSettings(requireContext(), SETTINGS_APPLICATION_VOICE_SPEED);
                setInputApplicationSpeechRateProgressSettings(requireContext(), SETTINGS_APPLICATION_VOICE_PROGRESS_SPEED);

                setInputApplicationPitchSettings(requireContext(), SETTINGS_APPLICATION_VOICE_PITCH);
                setInputApplicationPitchProgressSettings(requireContext(), SETTINGS_APPLICATION_VOICE_PROGRESS_PITCH);

                displayApplicationSettings();
            } else {
                setInputSystemSpeechRateSettings(requireContext(), SETTINGS_SYSTEM_VOICE_SPEED);
                setInputSystemSpeechRateProgressSettings(requireContext(), SETTINGS_SYSTEM_VOICE_PROGRESS_SPEED);

                setInputSystemPitchSettings(requireContext(), SETTINGS_SYSTEM_VOICE_PITCH);
                setInputSystemPitchProgressSettings(requireContext(), SETTINGS_SYSTEM_VOICE_PROGRESS_PITCH);

                displaySystemSettings();
            }

            destroyTextToSpeech();
            textToSpeech = new TextToSpeechHelper(requireContext());

        });


        binding.accessibilityPlay.setOnClickListener(v -> {
            if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(requireContext()))) {
                textToSpeech.speak("Isa itong halimbawa ng pangungusap sa wikang Filipino", TextToSpeech.QUEUE_FLUSH);
            } else {
                textToSpeech.speak("This is an example of speech synthesis in english", TextToSpeech.QUEUE_FLUSH);
            }
        });
    }

    @Override
    public void onDestroy() {
        destroyTextToSpeech();
        super.onDestroy();
    }

    private void destroyTextToSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.destroy();
        }
    }
}