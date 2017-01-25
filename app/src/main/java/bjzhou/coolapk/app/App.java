package bjzhou.coolapk.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import bjzhou.coolapk.app.services.UpgradeService;

/**
 * Created by bjzhou on 14-7-29.
 */
public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, UpgradeService.class));
        appContext = getApplicationContext();
    }

    public static Context getContext() {
        return appContext;
    }
}
