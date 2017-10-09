package com.daunkredit.program.sulu.app;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

public class ToastManager {

    static Toast toast;

    public static void showToast(String msg, int duration, int gravity) {

        if(toast == null){
            toast = Toast.makeText(App.instance, msg, duration);
        }

        toast.setGravity(gravity, 0, 0);
        toast.setDuration(duration);

        toast.setText(msg);

        toast.show();

    }

    public static void showToast(String msg, int duration) {
        showToast(msg, duration, Gravity.BOTTOM);
    }

    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT, Gravity.BOTTOM);
    }

    public static void showToastOnUIThread(final Activity act, final String msg, final int duration, final int gravity) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg, duration, gravity);
            }
        });
    }
}