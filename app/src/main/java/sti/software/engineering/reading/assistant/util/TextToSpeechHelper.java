package sti.software.engineering.reading.assistant.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;

import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputApplicationPitchSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputApplicationSpeechRateSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputSystemPitchSettings;
import static sti.software.engineering.reading.assistant.util.ApplicationSettings.getOutputSystemSpeechRateSettings;

//https://developer.android.com/reference/android/speech/tts/TextToSpeech#setVoice(android.speech.tts.Voice)
public class TextToSpeechHelper {

    private static final String TAG = "TextToSpeechHelper";

    public static final String UTTERANCE_ID_READING_TEXT = "sti.software.engineering.reading.assistant.READING";

    private static final float SPEECH_RATE = 1F;
    private static final float SPEECH_PITCH = 1.1f;

    private TextToSpeech textToSpeech;
    private boolean isSuccess;

    private OnUtteranceProgressListener listener;

    public interface OnUtteranceProgressListener {
        void onStart(String utteranceId);

        void onDone(String utteranceId);

        void onError(String utteranceId);
    }

    public void setOnUtteranceProgressListener(OnUtteranceProgressListener listener) {
        this.listener = listener;
    }

    public TextToSpeechHelper(Context context) {
        this.textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {

                this.isSuccess = true;

                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                this.textToSpeech.setSpeechRate(getOutputSystemSpeechRateSettings(context));
                this.textToSpeech.setPitch(getOutputSystemPitchSettings(context));

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    Log.e(TAG, "Language not supported.");
                }

                if (ApplicationSettings.SETTINGS_VOICE_APPLICATION.equalsIgnoreCase(ApplicationSettings.getOutputVoiceSettings(context))) {
                    Voice voice = getVoice();
                    if (voice != null) {
                        Log.d(TAG, "TextToSpeechHelper: voice " + voice.getName());
                        Log.d(TAG, "TextToSpeechHelper: speed " + getOutputApplicationSpeechRateSettings(context));
                        this.textToSpeech.setSpeechRate(getOutputApplicationSpeechRateSettings(context));
                        this.textToSpeech.setPitch(getOutputApplicationPitchSettings(context));
                        this.textToSpeech.setVoice(voice);
                    }
                }

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d(TAG, "onStart: utteranceId " + utteranceId);
                        listener.onStart(utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d(TAG, "onDone: utteranceId " + utteranceId);
                        listener.onDone(utteranceId);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.d(TAG, "onError: utteranceId " + utteranceId);
                        listener.onError(utteranceId);
                    }
                });


            } else Log.e(TAG, "Initialization Failed");
        }, "com.google.android.tts");

    }


    private Voice getVoice() {
        for (Voice tmpVoice : textToSpeech.getVoices()) {
//            Log.d(TAG, "getVoice: " + tmpVoice.getName() + " " + tmpVoice.getLocale());
            if (tmpVoice.getName().equals("fil-ph-x-cfc-network")) {
                return tmpVoice;
            }
        }
        return null;
    }

    public void speak(CharSequence text, int queueMode) {
        if (this.isSuccess)
            this.textToSpeech.speak(text, queueMode, null, null);
    }

    public void speak(CharSequence text, String utteranceId, int queueMode) {
        if (this.isSuccess)
            this.textToSpeech.speak(text, queueMode, null, utteranceId);
    }

    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public void destroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
