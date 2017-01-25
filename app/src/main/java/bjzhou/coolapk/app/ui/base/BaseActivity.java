package bjzhou.coolapk.app.ui.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bjzhou.coolapk.app.R;

public class BaseActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = new Random().nextInt(65535);
    private PermissionListener mPermissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void checkPermissions(PermissionListener listener, String... permissions) {
        this.mPermissionListener = listener;
        List<String> needRequests = new ArrayList<>();
        for (String permission : permissions) {
            boolean granted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
            boolean shouldRational = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
            if (!granted && !shouldRational) {
                needRequests.add(permission);
            } else if (!granted) {
                listener.onResult(permission, false);
            } else {
                listener.onResult(permission, true);
            }
        }
        if (needRequests.size() == 0) return;
        ActivityCompat.requestPermissions(this, needRequests.toArray(new String[needRequests.size()]), REQUEST_PERMISSION);
    }

    public interface PermissionListener {
        void onResult(String permission, boolean succeed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                mPermissionListener.onResult(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    protected void setActionBarTitle(int resId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(resId);
        }
    }

    protected void showHome(boolean showHome) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(showHome);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
