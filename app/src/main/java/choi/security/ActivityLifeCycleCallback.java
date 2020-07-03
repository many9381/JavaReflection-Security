package choi.security;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;

public class ActivityLifeCycleCallback implements Application.ActivityLifecycleCallbacks, Serializable {

    public Boolean TFCaptureLock = false;

    private ActivityLifeCycleCallback() {
    }

    private static class LazyHolder {
        public static final ActivityLifeCycleCallback INSTANCE = new ActivityLifeCycleCallback();
    }

    public static ActivityLifeCycleCallback getInstance() {
        return LazyHolder.INSTANCE;
    }


    // Capturelock Turn On/Off Switch
    public void switchCapturelock() {
        TFCaptureLock = !TFCaptureLock;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //Log.d(activity.getLocalClassName(), "CREATE !!");
        if(!TFCaptureLock) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

    }

    @Override
    public void onActivityResumed(Activity activity) {
        //Log.d(activity.getLocalClassName(), "RESUME !!");

        Toast.makeText(activity, TFCaptureLock.toString(), Toast.LENGTH_LONG).show();
        if(!TFCaptureLock) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //Log.d(activity.getLocalClassName(), "DESTORY !!");

    }

    @Override
    public void onActivityPaused(Activity activity) {
        //Log.d(activity.getLocalClassName(), "PAUSED !!");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //Log.d(activity.getLocalClassName(), "STOP !!");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }
}
