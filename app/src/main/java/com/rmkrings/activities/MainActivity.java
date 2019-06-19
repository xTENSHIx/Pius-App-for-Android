package com.rmkrings.activities;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Reachability;
import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.fragments.CalendarFragment;
import com.rmkrings.fragments.DashboardFragment;
import com.rmkrings.pius_app_for_android;
import com.rmkrings.fragments.VertretungsplanFragment;
import com.rmkrings.fragments.TodayFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements ReachabilityChangeCallback
{

    static public final String targetDashboard = "dashboard";

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new TodayFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_substitution_schedule: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new VertretungsplanFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_dashboard: {
                    startFragment(new DashboardFragment());
                    /*
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new DashboardFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    */
                    return true;
                }

                case R.id.navigation_calendar: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new CalendarFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_settings:
                    Intent a = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(a);
                    return false;
            }
            return false;
        }
    };

    private void startFragment(Fragment f, Boolean withBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, f);

        if (withBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void startFragment(Fragment f) {
        this.startFragment(f, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add navigation bar.
        BottomNavigationView navigation = findViewById(R.id.navigation);
        ColorStateList tint = (Reachability.isReachable())
                ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors)
                : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Reachability.getInstance().setReachabilityChangeCallback(this);

        // Show Today fragment initially.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new TodayFragment());
        transaction.commit();

        // If App is used for the very first time show information
        // that user should log in to Pius website in Settings.
        if (AppDefaults.hasShowIntro()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_welcome))
                    .setMessage(getResources().getString(R.string.text_intro))
                    .setPositiveButton(getResources().getString(R.string.label_start_now), null)
                    .show();
            AppDefaults.setShowIntro(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_home);

        try {
            int currentVersionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            if (currentVersionCode > AppDefaults.getVersionCode()) {
                // Any thing that need to be migrated goes in here.
                AppDefaults.setVersionCode(currentVersionCode);
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Start alternate fragment on request.
        String target = getIntent().getStringExtra("target");
        if (target != null && target.equals(targetDashboard)) {
            startFragment(new DashboardFragment(), false);
        }
    }

    @Override
    public void execute(boolean isReachable) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        ColorStateList tint = (isReachable)
                ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors)
                : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);

        navigation.refreshDrawableState();
    }
}