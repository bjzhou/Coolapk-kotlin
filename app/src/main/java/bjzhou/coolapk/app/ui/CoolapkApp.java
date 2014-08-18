package bjzhou.coolapk.app.ui;

import android.app.Application;
import android.content.Intent;
import bjzhou.coolapk.app.services.UpgradeService;

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
