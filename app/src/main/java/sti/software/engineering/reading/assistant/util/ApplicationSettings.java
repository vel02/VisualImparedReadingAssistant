package sti.software.engineering.reading.assistant.util;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class ApplicationSettings {

    public static final String APPLICATION_SETTINGS_CHANGE_VOICE = "sti.software.engineering.reading.assistant.CHANGE_VOICE";

    public static void setInputVoiceSettings(Context context, boolean activate) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(APPLICATION_SETTINGS_CHANGE_VOICE, activate)
                .apply();
    }

    public static boolean getOutputVoiceSettings(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(APPLICATION_SETTINGS_CHANGE_VOICE, false);
    }

}
