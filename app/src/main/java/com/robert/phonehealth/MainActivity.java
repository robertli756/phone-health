package com.robert.phonehealth;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView storageText, ramText, batteryText, junkSummary;
    private ProgressBar storageBar, ramBar;
    private LinearLayout junkList;
    private final List<JunkItem> junkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
        refreshDashboard();
        scheduleDailyReminder();
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        root.setPadding(pad, pad, pad, pad);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText(Strings.appName());
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        root.addView(sectionLabel(Strings.storage()));
        storageBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        root.addView(storageBar);
        storageText = new TextView(this);
        root.addView(storageText);

        root.addView(sectionLabel(Strings.ram()));
        ramBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        root.addView(ramBar);
        ramText = new TextView(this);
        root.addView(ramText);

        root.addView(sectionLabel(Strings.battery()));
        batteryText = new TextView(this);
        root.addView(batteryText);

        root.addView(sectionLabel(Strings.cleanup()));
        junkSummary = new TextView(this);
        junkSummary.setText(Strings.junkHint());
        root.addView(junkSummary);

        Button scanBtn = new Button(this);
        scanBtn.setText(Strings.scan());
        scanBtn.setOnClickListener(v -> scanJunk());
        root.addView(scanBtn);

        junkList = new LinearLayout(this);
        junkList.setOrientation(LinearLayout.VERTICAL);
        root.addView(junkList);

        Button delBtn = new Button(this);
        delBtn.setText(Strings.deleteSelected());
        delBtn.setOnClickListener(v -> deleteSelected());
        root.addView(delBtn);

        root.addView(sectionLabel(Strings.shortcuts()));
        Button sysStorageBtn = new Button(this);
        sysStorageBtn.setText(Strings.openStorage());
        sysStorageBtn.setOnClickListener(v -> openSystemStorage());
        root.addView(sysStorageBtn);

        Button appsBtn = new Button(this);
        appsBtn.setText(Strings.manageApps());
        appsBtn.setOnClickListener(v -> openAppSettings());
        root.addView(appsBtn);

        TextView note = new TextView(this);
        note.setText(Strings.note());
        note.setTextSize(12);
        root.addView(note);

        setContentView(scroll);
    }

    private TextView sectionLabel(String s) {
        TextView t = new TextView(this);
        t.setText("\n" + s);
        t.setTextSize(18);
        return t;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void refreshDashboard() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = stat.getBlockSizeLong();
        long total = stat.getBlockCountLong() * blockSize;
        long free = stat.getAvailableBlocksLong() * blockSize;
        long used = total - free;
        storageBar.setMax(100);
        storageBar.setProgress((int) (used * 100 / total));
        storageText.setText(String.format(Locale.US, Strings.usedOf(), fmt(used), fmt(total)));

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long ramUsed = mi.totalMem - mi.availMem;
        ramBar.setMax(100);
        ramBar.setProgress((int) (ramUsed * 100 / mi.totalMem));
        ramText.setText(String.format(Locale.US, Strings.usedOf(), fmt(ramUsed), fmt(mi.totalMem)));

        Intent b = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        StringBuilder sb = new StringBuilder();
        if (b != null) {
            int level = b.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = b.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int pct = level * 100 / scale;
            int temp = b.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
            int status = b.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
            int health = b.getIntExtra(BatteryManager.EXTRA_HEALTH,
                    BatteryManager.BATTERY_HEALTH_UNKNOWN);
            sb.append(Strings.batteryWord()).append(" ").append(pct).append("%")
              .append(charging ? Strings.charging() : "")
              .append("\n").append(Strings.tempWord()).append(temp).append("°C")
              .append("\n").append(Strings.healthWord()).append(healthStr(health));
            if (temp >= 40) sb.append(Strings.tempHigh());
        }
        batteryText.setText(sb.toString());
    }

    private String healthStr(int h) {
        switch (h) {
            case BatteryManager.BATTERY_HEALTH_GOOD: return Strings.healthGood();
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: return Strings.healthOverheat();
            case BatteryManager.BATTERY_HEALTH_COLD: return Strings.healthCold();
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: return Strings.healthOverVolt();
            case BatteryManager.BATTERY_HEALTH_DEAD: return Strings.healthDead();
            default: return Strings.healthUnknown();
        }
    }

    private String fmt(long bytes) {
        if (bytes >= 1L << 30) return String.format(Locale.US, "%.1f GB", bytes / (double) (1L << 30));
        if (bytes >= 1L << 20) return String.format(Locale.US, "%.1f MB", bytes / (double) (1L << 20));
        return String.format(Locale.US, "%d KB", bytes >> 10);
    }

    private void scanJunk() {
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            Toast.makeText(this, Strings.needPermission(), Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
            return;
        }
        junkItems.clear();
        junkList.removeAllViews();
        List<File> roots = new ArrayList<>();
        File dl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (dl != null) roots.add(dl);
        File ext = Environment.getExternalStorageDirectory();
        if (ext != null) roots.add(ext);
        for (File r : roots) scanDir(r, 0);
        if (junkItems.isEmpty()) {
            junkSummary.setText(Strings.cleanNone());
            return;
        }
        long totalJunk = 0;
        for (JunkItem j : junkItems) totalJunk += j.file.length();
        junkSummary.setText(String.format(Locale.US, Strings.foundJunk(),
                junkItems.size(), fmt(totalJunk)));
        for (JunkItem j : junkItems) {
            CheckBox cb = new CheckBox(this);
            cb.setText(j.file.getName() + "  (" + fmt(j.file.length()) + ")");
            cb.setChecked(true);
            final JunkItem item = j;
            cb.setOnCheckedChangeListener((v, checked) -> item.checked = checked);
            junkList.addView(cb);
        }
    }

    private void scanDir(File dir, int depth) {
        if (dir == null || depth > 2 || junkItems.size() > 200) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                File[] kids = f.listFiles();
                if (kids != null && kids.length == 0
                        && !f.getAbsolutePath().contains("/Android/")) {
                    junkItems.add(new JunkItem(f));
                } else {
                    scanDir(f, depth + 1);
                }
            } else {
                String n = f.getName().toLowerCase(Locale.US);
                if (n.endsWith(".apk") || n.endsWith(".tmp") || n.endsWith(".temp")
                        || n.endsWith(".log") || n.endsWith(".bak") || n.endsWith(".dmp")) {
                    junkItems.add(new JunkItem(f));
                } else if (f.length() > 100L * 1024 * 1024
                        && !f.getAbsolutePath().contains("/Android/")) {
                    junkItems.add(new JunkItem(f));
                }
            }
        }
    }

    private void deleteSelected() {
        int count = 0;
        long freed = 0;
        for (JunkItem j : junkItems) {
            if (j.checked) {
                long sz = j.file.length();
                if (deleteRecursive(j.file)) {
                    count++;
                    freed += sz;
                }
            }
        }
        Toast.makeText(this, String.format(Locale.US, Strings.deleted(), count, fmt(freed)),
                Toast.LENGTH_LONG).show();
        scanJunk();
        refreshDashboard();
    }

    private boolean deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] kids = f.listFiles();
            if (kids != null) for (File k : kids) deleteRecursive(k);
        }
        return f.delete();
    }

    private void openSystemStorage() {
        try {
            startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void openAppSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void scheduleDailyReminder() {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 21);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    private static class JunkItem {
        final File file;
        boolean checked = true;
        JunkItem(File f) { file = f; }
    }
}
