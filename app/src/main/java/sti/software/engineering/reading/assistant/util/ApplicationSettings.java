package sti.software.engineering.reading.assistant.util;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class ApplicationSettings {

    public static final String APPLICATION_SETTINGS_CHANGE_VOICE = "sti.software.engineering.reading.assistant.CHANGE_VOICE";

    public static final String SETTINGS_VOICE_APPLICATION = "APPLICATION_VOICE";
    public static final String SETTINGS_VOICE_SYSTEM = "SYSTEM_VOICE";

    public static final String INSTANTIATE_SETTINGS_TTS = "sti.software.engineering.reading.assistant.INSTANTIATE_TTS";

    public static final String SETTINGS_INSTANTIATE_TTS_YES = "INSTANTIATE_TTS_YES";
    public static final String SETTINGS_INSTANTIATE_TTS_NO = "INSTANTIATE_TTS_NO";

    public static final String READING_SETTINGS_STATE_TTS = "sti.software.engineering.reading.assistant.SETTINGS_STATE_TTS";

    public static final String SETTINGS_READING_STATE_TTS_YES = "READING_YES";
    public static final String SETTINGS_READING_STATE_TTS_NO = "READING_NO";


    public static final String APPLICATION_SETTINGS_CHANGE_PROGRESS_SPEED = "sti.software.engineering.reading.assistant.CHANGE_APPLICATION_PROGRESS_SPEED";
    public static final String APPLICATION_SETTINGS_CHANGE_PROGRESS_PITCH = "sti.software.engineering.reading.assistant.CHANGE_APPLICATION_PROGRESS_PITCH";
    public static final String SYSTEM_SETTINGS_CHANGE_PROGRESS_SPEED = "sti.software.engineering.reading.assistant.CHANGE_SYSTEM_PROGRESS_SPEED";
    public static final String SYSTEM_SETTINGS_CHANGE_PROGRESS_PITCH = "sti.software.engineering.reading.assistant.CHANGE_SYSTEM_PROGRESS_PITCH";

    public static final String APPLICATION_SETTINGS_CHANGE_SPEED = "sti.software.engineering.reading.assistant.CHANGE_APPLICATION_SPEED";
    public static final String APPLICATION_SETTINGS_CHANGE_PITCH = "sti.software.engineering.reading.assistant.CHANGE_APPLICATION_PITCH";
    public static final float SETTINGS_APPLICATION_VOICE_SPEED = 1F;
    public static final float SETTINGS_APPLICATION_VOICE_PITCH = 1F;

    public static final String SYSTEM_SETTINGS_CHANGE_SPEED = "sti.software.engineering.reading.assistant.CHANGE_SYSTEM_SPEED";
    public static final String SYSTEM_SETTINGS_CHANGE_PITCH = "sti.software.engineering.reading.assistant.CHANGE_SYSTEM_PITCH";
    public static final float SETTINGS_SYSTEM_VOICE_SPEED = 0.8F;
    public static final float SETTINGS_SYSTEM_VOICE_PITCH = 1.1F;

    public static final int SETTINGS_APPLICATION_VOICE_PROGRESS_SPEED = 50;
    public static final int SETTINGS_APPLICATION_VOICE_PROGRESS_PITCH = 50;
    public static final int SETTINGS_SYSTEM_VOICE_PROGRESS_SPEED = 40;
    public static final int SETTINGS_SYSTEM_VOICE_PROGRESS_PITCH = 55;


    public static void setInputVoiceSettings(Context context, String settings) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(APPLICATION_SETTINGS_CHANGE_VOICE, settings)
                .apply();
    }

    public static String getOutputVoiceSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(APPLICATION_SETTINGS_CHANGE_VOICE, SETTINGS_VOICE_APPLICATION);
    }

    public static void setInputReInstantiateTTSSettings(Context context, String settings) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(INSTANTIATE_SETTINGS_TTS, settings)
                .apply();
    }

    public static String getOutputReInstantiateTTSSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(INSTANTIATE_SETTINGS_TTS, SETTINGS_INSTANTIATE_TTS_NO);
    }

    public static void setInputReadingStateTTSSettings(Context context, String settings) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(READING_SETTINGS_STATE_TTS, settings)
                .apply();
    }

    public static String getOutputReadingStateTTSSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(READING_SETTINGS_STATE_TTS, SETTINGS_READING_STATE_TTS_NO);
    }

    //Application language

    public static void setInputApplicationSpeechRateSettings(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putFloat(APPLICATION_SETTINGS_CHANGE_SPEED, value)
                .apply();
    }

    public static float getOutputApplicationSpeechRateSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(APPLICATION_SETTINGS_CHANGE_SPEED, SETTINGS_APPLICATION_VOICE_SPEED);
    }

    public static void setInputApplicationPitchSettings(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putFloat(APPLICATION_SETTINGS_CHANGE_PITCH, value)
                .apply();
    }

    public static float getOutputApplicationPitchSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(APPLICATION_SETTINGS_CHANGE_PITCH, SETTINGS_APPLICATION_VOICE_PITCH);
    }

    //System language
    public static void setInputSystemSpeechRateSettings(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putFloat(SYSTEM_SETTINGS_CHANGE_SPEED, value)
                .apply();
    }

    public static float getOutputSystemSpeechRateSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(SYSTEM_SETTINGS_CHANGE_SPEED, SETTINGS_SYSTEM_VOICE_SPEED);
    }

    public static void setInputSystemPitchSettings(Context context, float value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putFloat(SYSTEM_SETTINGS_CHANGE_PITCH, value)
                .apply();
    }

    public static float getOutputSystemPitchSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(SYSTEM_SETTINGS_CHANGE_PITCH, SETTINGS_SYSTEM_VOICE_PITCH);
    }

    //Application language
    public static void setInputApplicationSpeechRateProgressSettings(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(APPLICATION_SETTINGS_CHANGE_PROGRESS_SPEED, value)
                .apply();
    }

    public static int getOutputApplicationSpeechRateProgressSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(APPLICATION_SETTINGS_CHANGE_PROGRESS_SPEED, SETTINGS_APPLICATION_VOICE_PROGRESS_SPEED);
    }

    public static void setInputApplicationPitchProgressSettings(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(APPLICATION_SETTINGS_CHANGE_PROGRESS_PITCH, value)
                .apply();
    }

    public static int getOutputApplicationPitchProgressSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(APPLICATION_SETTINGS_CHANGE_PROGRESS_PITCH, SETTINGS_APPLICATION_VOICE_PROGRESS_PITCH);
    }

    //System language
    public static void setInputSystemSpeechRateProgressSettings(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(SYSTEM_SETTINGS_CHANGE_PROGRESS_SPEED, value)
                .apply();
    }

    public static int getOutputSystemSpeechRateProgressSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(SYSTEM_SETTINGS_CHANGE_PROGRESS_SPEED, SETTINGS_SYSTEM_VOICE_PROGRESS_SPEED);
    }

    public static void setInputSystemPitchProgressSettings(Context context, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(SYSTEM_SETTINGS_CHANGE_PROGRESS_PITCH, value)
                .apply();
    }

    public static int getOutputSystemPitchProgressSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(SYSTEM_SETTINGS_CHANGE_PROGRESS_PITCH, SETTINGS_SYSTEM_VOICE_PROGRESS_PITCH);
    }
}
