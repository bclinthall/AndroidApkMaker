/*
 * Copyright (C) 2016 B. Clint Hall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theaetetuslabs.android_apkmaker;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;

import com.theaetetuslabs.java_apkmaker.ApkMaker.Callbacks;
import com.theaetetuslabs.java_apkmaker.Logger;
import com.theaetetuslabs.java_apkmaker.ApkMaker.ApkMakerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.Locale;

import kellinwood.security.zipsigner.ZipSigner;

/**
 * Created by bclinthall on 8/19/16.
 */
public class AndroidApkMaker {
    public static final int ONGOING_NOTIFICATION_ID = 1;
    private static final int DONE_NOTIFICATION_ID = 2;

    IntentService service;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    static String newAppName;
    static String newAppPackage;
    static AfterInstallDialogAdder adder;
    String projectDirectoryPath;
    boolean verbose;
    BuildFiles buildFiles;
    public AndroidApkMaker(IntentService service, 
                           NotificationManager mNotifyManager, 
                           NotificationCompat.Builder mBuilder){
        this.service = service;
        this.mNotifyManager = mNotifyManager;
        this.mBuilder = mBuilder;
    }
    public void make(String projectDirectoryPath, boolean verbose){
        make(null, null, projectDirectoryPath, verbose, false);
    }
    public void make(String newAppName, String newAppPackage, String projectDirectoryPath, boolean promptForInstall, boolean verbose){
        make(newAppName, newAppPackage, projectDirectoryPath, promptForInstall, verbose, null);

    }
    public void make(String newAppName, String newAppPackage, String projectDirectoryPath, boolean verbose, AfterInstallDialogAdder adder){
        make(newAppName, newAppPackage, projectDirectoryPath, true, verbose, adder);
    }
    public void make(String newAppName, String newAppPackage, String projectDirectoryPath, boolean promptForInstall, boolean verbose, AfterInstallDialogAdder adder){
        this.adder = adder;
        AndroidApkMaker.newAppName = newAppName;
        AndroidApkMaker.newAppPackage = newAppPackage;
        this.projectDirectoryPath = projectDirectoryPath;
        this.verbose = verbose;

        buildFiles = new BuildFiles(service).clear();
        try {
            unpackAsset(service, "android.jar", buildFiles.androidJar);
        } catch (IOException e) {
            Logger.trace(e, verbose, System.err);;
            sendFailNotification(service.getString(R.string.upack_aapt_failed));
            return;
        }
        try {
            unpackAapt(service, buildFiles.aapt);
        } catch (IOException e) {
            Logger.trace(e, verbose, System.err);;
            sendFailNotification(service.getString(R.string.upack_androidJar_failed));
            return;
        }

        makeApk(mNotifyManager, mBuilder, projectDirectoryPath, verbose);

        try {
            signApk();
        } catch (Exception e) {
            Logger.trace(e, verbose, System.err);;
            sendFailNotification(service.getString(R.string.sign_failed) +": "+ e.getMessage());
        }

        if(promptForInstall && buildFiles.signed.exists()){
            sendInstallNotification();
        }else{
            sendFailNotification("signed.apk doesn't exist");
        }
    }
    private static File unpackAsset(Context context, String assetName, File dest) throws IOException {
        if(!dest.exists()){
            InputStream assetIn = context.getAssets().open(assetName);
            dest.createNewFile();
            int length = 0;
            byte[] buffer = new byte[4096];
            FileOutputStream rawOut = new FileOutputStream(dest);
            while ((length = assetIn.read(buffer)) > 0) {
                rawOut.write(buffer, 0, length);
            }
            rawOut.flush();
            rawOut.close();
            assetIn.close();
        }
        return dest;
    }
    private static File unpackAapt(Context context, File aapt) throws IOException {
        if (!aapt.exists()) {
            String aaptToUse = null;
            boolean usePie = VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
            String abi;
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
                String[] abis = Build.SUPPORTED_32_BIT_ABIS;
                for (String mAbi : abis) {
                    aaptToUse = getAaptFlavor(mAbi, usePie);
                    if (aaptToUse != null) {
                        break;
                    }
                }
            } else {
                aaptToUse = getAaptFlavor(Build.CPU_ABI, usePie);
            }
            if (aaptToUse == null) {
                aaptToUse = "aapt-arm";
            }
            if (usePie) {
                aaptToUse += "-pie";
            }
            unpackAsset(context, aaptToUse, aapt);
        }
        if(!aapt.canExecute()){
            aapt.setExecutable(true, true);
        }
        if(!aapt.canExecute()) {
            Runtime.getRuntime().exec("chmod 777 " + aapt.getAbsolutePath());
        }
        return aapt;
    }

    private static String getAaptFlavor(String abi, boolean usePie) {
        abi = abi.substring(0, 3).toLowerCase(Locale.ENGLISH);
        String aaptToUse = null;
        if (abi.equals("arm")) {
            aaptToUse = "aapt-arm";
        } else if (abi.equals("x86")) {
            aaptToUse = "aapt-x86";
        } else if (abi.equals("mip") && !usePie) {
            aaptToUse = "aapt-mip";
        }
        return aaptToUse;
    }

    private void makeApk(final NotificationManager mNotifyManager,
                         final NotificationCompat.Builder mBuilder,
                         String projectDirPath,
                         boolean verbose){
        ApkMakerOptions options = new ApkMakerOptions();
        options.aapt = buildFiles.aapt.getAbsolutePath();
        options.androidJar = buildFiles.androidJar.getAbsolutePath();
        options.projectDir = projectDirPath;
        options.outputFile = buildFiles.unsigned.getAbsolutePath();
        options.verbose = verbose;
        options.aaptRunner = null;/*new AaptRunner(){
            @Override
            public boolean runAapt(File androidManifest, File resourcesArsc, File androidJar, File resDir, File genDir, Callbacks callbacks) {
                String logDirPath = new File(service.getFilesDir(), "logDir").getAbsolutePath();
                Log.d("TAG", "LogDirPath: " + logDirPath);
                String aaptCommand = "package " +
                        " -f " + //force overwrite existing files;
                        " -v " +//verbose
                        " -M " + androidManifest.getAbsolutePath() +
                        " -F " + resourcesArsc.getAbsolutePath() +  //this is where aapt will output the resource file to go in apk
                        " -I " + androidJar.getAbsolutePath() +
                        " -S " + resDir.getAbsolutePath() +
                        " -J " + genDir.getAbsolutePath();  //where to put R.java

                new TArnAapt(logDirPath).fnExecute(aaptCommand);
                return false;
            }
        };*/
        com.theaetetuslabs.java_apkmaker.Main.main(options, null, null, new Callbacks() {
            @Override
            public void updateProgress(String msg, float percent) {
                if(percent==-1){
                    mBuilder.setProgress(0, 0, true);
                }else{
                    percent *= 100;
                    mBuilder.setProgress(100, (int) percent, false);
                    DecimalFormat df = new DecimalFormat("##");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    msg = msg + ". " + df.format(percent) + "%";
                }
                mBuilder.setContentText(msg);
                mNotifyManager.notify(ONGOING_NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void error(String str) {
                sendFailNotification(str);
            }

            @Override
            public void done(File apk) {

            }
        });
    }
    private void signApk() throws IOException, GeneralSecurityException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        Logger.logd( "ZipSigner.MODE_AUTO_TESTKEY: " + ZipSigner.MODE_AUTO_TESTKEY, verbose, System.out);
        ZipSigner zipsigner = new ZipSigner();
        zipsigner.setKeymode(ZipSigner.MODE_AUTO_TESTKEY);
        zipsigner.signZip(buildFiles.unsigned.getAbsolutePath(), buildFiles.signed.getAbsolutePath());
        Logger.logd("Signed zip: " + buildFiles.signed.getAbsolutePath() + " exists: " + buildFiles.signed.exists(), verbose, System.out);
    }






    private void sendFailNotification(String contentText){
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(service)
                .setAutoCancel(true)
                .setContentTitle(getStringReplacing(R.string.building_failed, "{{appName}}", newAppName))
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.stat_notify_error);

        InstallActivity.setNeedStartInstall(true);
        Intent resultIntent = new Intent(service, InstallActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(service, 77, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager doneNotifyManager =
                (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        doneNotifyManager.notify(DONE_NOTIFICATION_ID, mBuilder.build());
    }
    private void sendInstallNotification(){
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(service)
                .setAutoCancel(true)
                .setContentTitle(getStringReplacing(R.string.building_done, "{{appName}}", newAppName))
                .setContentText(service.getString(R.string.tap_to_install))
                .setSmallIcon(R.drawable.ic_done);

        InstallActivity.setNeedStartInstall(true);
        Intent resultIntent = new Intent(service, InstallActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(service, 77, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager doneNotifyManager =
                (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        doneNotifyManager.notify(DONE_NOTIFICATION_ID, mBuilder.build());

    }
    private String getStringReplacing(int id, String oldStr, String newStr){
        return service.getString(id).replace(oldStr, newStr);
    }
    public interface AfterInstallDialogAdder{
        public void addToAfterInstallDialog(AlertDialog.Builder builder, InstallActivity installActivity);
    }

}
