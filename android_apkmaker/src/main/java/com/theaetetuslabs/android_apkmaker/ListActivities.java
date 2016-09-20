package com.theaetetuslabs.android_apkmaker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by bclinthall on 8/20/16.
 */
public class ListActivities {
    public static void listActivities(Context context) {
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> installedPackages = pm
                .getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            String packageName = pi.packageName;

            try {
                pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES
                        | PackageManager.GET_PROVIDERS);
            } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                continue;
            }
            if (pi.activities != null) {
                for (ActivityInfo ci : pi.activities) {
                    if(ci.name.contains("theaetetuslabs")) Log.d("TAG", ci.name);
                }
            }
        }
    }
}
