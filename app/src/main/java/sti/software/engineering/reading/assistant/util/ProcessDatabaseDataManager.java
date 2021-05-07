package sti.software.engineering.reading.assistant.util;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import sti.software.engineering.reading.assistant.ui.home.sub.HomeFragmentViewModel;

public class ProcessDatabaseDataManager extends CountDownTimer {

    private static final String TAG = "CountDownTimerHelper";

    private static ProcessDatabaseDataManager instance;

    private final ViewModel viewModel;

    private ProcessDatabaseDataManager(ViewModel viewModel, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.viewModel = viewModel;
    }

    public static ProcessDatabaseDataManager refresh(ViewModel viewModel, long millisInFuture, long countDownInterval) {
        if (instance == null)
            instance = new ProcessDatabaseDataManager(viewModel, millisInFuture, countDownInterval);
        return instance;
    }


    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: " + (millisUntilFinished / 1000));
    }

    @Override
    public void onFinish() {
        if (viewModel instanceof HomeFragmentViewModel) {
            ((HomeFragmentViewModel) viewModel).processDatabaseData();
        }
    }
}
