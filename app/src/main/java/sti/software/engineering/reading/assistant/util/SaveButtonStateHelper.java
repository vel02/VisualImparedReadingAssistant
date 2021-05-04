package sti.software.engineering.reading.assistant.util;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class SaveButtonStateHelper {

    public static final String SAVE_STATE_REQUEST = "sti.software.engineering.reading.assistant.SAVE_STATE_REQUEST";

    private static SaveButtonStateHelper instance;

    private SaveButtonStateHelper() {
    }

    public static SaveButtonStateHelper getInstance() {
        if (instance == null) instance = new SaveButtonStateHelper();
        return instance;
    }

    public void setSaveButtonState(Context context, boolean enable) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(SAVE_STATE_REQUEST, enable)
                .apply();
    }

    public boolean getSaveButtonState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SAVE_STATE_REQUEST, false);
    }

}
