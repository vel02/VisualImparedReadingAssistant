package sti.software.engineering.reading.assistant.util;

import android.os.CountDownTimer;
import android.util.Log;

import sti.software.engineering.reading.assistant.ui.home.HomeViewModel;

public class ProcessDatabaseDataManager extends CountDownTimer {

    private static final String TAG = "CountDownTimerHelper";

    private static ProcessDatabaseDataManager instance;

    private final HomeViewModel viewModel;

    private ProcessDatabaseDataManager(HomeViewModel viewModel, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.viewModel = viewModel;
    }

    public static ProcessDatabaseDataManager refresh(HomeViewModel viewModel, long millisInFuture, long countDownInterval) {
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
        viewModel.processDatabaseData();
    }
}
