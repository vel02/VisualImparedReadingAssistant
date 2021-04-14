package sti.software.engineering.reading.assistant.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

//https://developer.android.com/reference/android/speech/tts/TextToSpeech#setVoice(android.speech.tts.Voice)
public class TextToSpeechHelper {

    private static final String TAG = "TextToSpeechHelper";

    private static final float SPEECH_RATE = 0.8F;
    private static final float SPEECH_PITCH = 1.1f;

    private static TextToSpeechHelper instance;
    private TextToSpeech textToSpeech;
    private boolean isSuccess;

    public static TextToSpeechHelper getInstance(Context context) {
        if (instance == null)
            instance = new TextToSpeechHelper(context);
        return instance;
    }

    private TextToSpeechHelper(Context context) {
        this.textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                this.isSuccess = true;
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                this.textToSpeech.setSpeechRate(SPEECH_RATE);
                this.textToSpeech.setPitch(SPEECH_PITCH);

                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    Log.e(TAG, "Language not supported.");
                }
            } else Log.e(TAG, "Initialization Failed");
        });
    }

    public void speak(CharSequence text, int queueMode) {
        if (this.isSuccess)
            this.textToSpeech.speak(text, queueMode, null, null);
    }

    public void destroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
