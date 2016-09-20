package com.theaetetuslabs.android_apkmaker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.io.File;

/**
 * Created by bclinthall on 8/18/16.
 */
public class ApkMakerService extends IntentService {
    private BuildFiles buildFiles;
    private boolean verbose;
    public ApkMakerService() {
        super("ApkMakerService");
    }

    public static void testMakeApk(Context context){
        File projectDirectory = new File(context.getFilesDir(), "src");
        start(context, projectDirectory, "Icon Pack", "com.theaetetuslabs.iconeffects.iconpack0", true);
    }
    public static void start(Context context, File projectDirectory,  String newAppName, String newAppPackage, boolean verbose){
        Intent intent = new Intent(context, ApkMakerService.class);
        intent.putExtra("projectDirectory", projectDirectory.getAbsolutePath());
        intent.putExtra("verbose", verbose);
        intent.putExtra("newAppName", newAppName);
        intent.putExtra("newAppPackage", newAppPackage);
        context.startService(intent);
    }
    public static void start(Context context, File projectDirectory, String newAppName, String newAppPackage){
        start(context, projectDirectory, newAppName, newAppPackage, false);
    }
    /**
        A component calls startService with an intent specifying this service.
        This service is started if need be.  The intent is put in a queue.
        When it is the intent's turn, it is passed to a bg thread where this method is
        called.  When the queue is empty, the service is stopped.
        This method should never be called manually.
        @param intent The intent that has just been popped off the queue and passed to this method.
    */
    @Override
    protected void onHandleIntent(Intent intent) {

        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getString(R.string.building_apk_title))
                .setContentText(getString(R.string.building_started))
                .setSmallIcon(R.drawable.ic_build);
        startForeground(AndroidApkMaker.ONGOING_NOTIFICATION_ID, mBuilder.build());
        String newAppName = intent.getStringExtra("newAppName");
        String newAppPackage = intent.getStringExtra("newAppPackage");
        String projectDirectory = intent.getStringExtra("projectDirectory");
        boolean verbose = intent.getBooleanExtra("verbose", false);
        new AndroidApkMaker(this, mNotifyManager, mBuilder).make(newAppName, newAppPackage, projectDirectory, verbose);
    }

}
