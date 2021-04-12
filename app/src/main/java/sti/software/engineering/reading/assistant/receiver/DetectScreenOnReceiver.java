/*
 *
 *   Created by Austria, Ariel Namias on 2/20/21 7:49 AM
 *   Copyright Ⓒ 2021. All rights reserved Ⓒ 2021 https://github.com/vel02
 *   Last modified: 2/20/21 7:08 AM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package sti.software.engineering.reading.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class DetectScreenOnReceiver extends BroadcastReceiver {

    private static final String TAG = "DetectScreenOnReceiver";
    private static final int THRESHOLD_SCREEN_ON = 3;

    private Vibrator vibrator;
    private OnScreenReceiverCallback callback;

    private int screenOnCount = 0;
    private long timestamp = 0;

    public void onScreenReceiverCallback(OnScreenReceiverCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (vibrator == null)
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (timestamp == 0) timestamp = (System.currentTimeMillis() / 1000);

        int count = 0;
        if (timestamp != 0) {
            long time = (System.currentTimeMillis() / 1000);
            count = (int) (time - timestamp);

            if (count > 5) {
                screenOnCount = 0;
                timestamp = 0;
            }
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d(TAG, "onReceive: SCREEN ON CALLED");
            Log.d(TAG, "onReceive: INTERVAL: " + count);
            screenOnCount++;
            if (screenOnCount == THRESHOLD_SCREEN_ON) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else vibrator.vibrate(500);
                callback.onTriggered();
            }
        }
    }

    public interface OnScreenReceiverCallback {
        void onTriggered();
    }
}
