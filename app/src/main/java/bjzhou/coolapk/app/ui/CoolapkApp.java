package bjzhou.coolapk.app.ui;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import bjzhou.coolapk.app.services.UpgradeService;

import java.util.Calendar;

/**
 * Created by bjzhou on 14-7-29.
 */
public class CoolapkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        silentUpgrade();
    }

    private void silentUpgrade() {
        Intent intent = new Intent(this, UpgradeService.class);
        startService(intent);
    }
}
