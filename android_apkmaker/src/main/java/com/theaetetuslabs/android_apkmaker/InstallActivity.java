package com.theaetetuslabs.android_apkmaker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.theaetetuslabs.javaapkmaker.Logger;
import com.theaetetuslabs.launcher.Launcher;
import com.theaetetuslabs.launcher.Launcher.AfterApply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InstallActivity extends AppCompatActivity {
    private MoveApkTask moveApkTask;
    AlertDialog afterInstallDialog;
    AlertDialog tryAgainDialog;
    BuildFiles files;
    public static boolean verbose = true;
    
    static void setNeedStartInstall(boolean needStartInstall) {
        InstallActivity.needStartInstall = needStartInstall;
    }

    private static boolean needStartInstall = false;
    static final int INSTALL_REQUEST = 1;


    protected void onInstallError(){
        //Error.  probably caused by TopDogCheck deleting APK before user
        //tried to install it.
        if(tryAgainDialog!=null) tryAgainDialog.cancel();
        new AlertDialog.Builder(this)
                .setMessage(R.string.retry_install)
                .setPositiveButton(R.string.retry, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveApk();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BuildFiles.deleteFiles(files.signed,files.extApk);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    protected void onInstallSuccess(){
        if(afterInstallDialog!=null) afterInstallDialog.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getStringReplacing(R.string.installed, "{{appName}}", AndroidApkMaker.newAppName))
                .setPositiveButton(getString(R.string.open), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getPackageManager().getLaunchIntentForPackage(AndroidApkMaker.newAppPackage);
                        if(intent!=null){
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .setNegativeButton(R.string.done, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false);
        addSpecificStuff(builder);
        afterInstallDialog = builder.show();
    }
    protected void addSpecificStuff(AlertDialog.Builder builder){
        //IconEffects specific stuff.
        builder.setMessage(R.string.iconeffects_msg);
        builder.setNeutralButton(R.string.apply, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Launcher.launcherApplyOrGet(InstallActivity.this, new AfterApply() {
                    @Override
                    public void afterApply() {
                        finish();
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.logd("ResultCode: " + resultCode + "; requestCode: " + requestCode, verbose, System.out);
        if(requestCode == INSTALL_REQUEST) {
            BuildFiles.deleteFiles(files.extApk);
            if(resultCode!=1){
                BuildFiles.deleteFiles(files.signed);
            }
            if(resultCode == 1){
                onInstallError();
            }else if(resultCode == -1){
                onInstallSuccess();
            }else{
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "============Install Activity Created");

        setContentView(R.layout.activity_install);
        Timer timer = new Timer();
        timer.schedule(new TopDogCheck(timer, this), 100);
        files = new BuildFiles(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(afterInstallDialog!=null) afterInstallDialog.cancel();
        if(tryAgainDialog!=null) tryAgainDialog.cancel();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Logger.logd("InstallActivity#onResume. signed path: " + files.signed.getAbsolutePath(), verbose, System.out);
        if(needStartInstall){
            SideLoadChecker slc = new SideLoadChecker(this);

            if(!files.signed.exists()){
                finish();
                return;
            }
            if(slc.canSideLoad()) {
                needStartInstall = false;
                new MoveApkTask().execute();
            }else{
                slc.requestSideLoad();
            }
        }
    }
    private String getStringReplacing(int id, String oldStr, String newStr){
        return getString(id).replace(oldStr, newStr);
    }

    private void promptInstall(){
        File apk = files.extApk;
        if(apk.exists()) {
            Intent promptInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            promptInstall.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            promptInstall.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            promptInstall.setData(Uri.fromFile(apk));
            startActivityForResult(promptInstall, INSTALL_REQUEST);
        }
    }
    private void moveApk(){
        if(moveApkTask==null){
            new MoveApkTask().execute();
        }
    }
    private class MoveApkTask extends AsyncTask<Void, Void, Boolean>{
        void copy(File src, File dst) throws IOException {
            if (!dst.exists()) {
                dst.createNewFile();
            }
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            BuildFiles files = new BuildFiles(InstallActivity.this);
            try {
                copy(files.signed, files.extApk);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                promptInstall();
            }else{
                BuildFiles.deleteFiles(files.extApk);
            }
            moveApkTask = null;
        }
    }

    public static class SideLoadChecker{
        private Context context;
        public SideLoadChecker(Context context){
            this.context = context;
        }
        public void check(){
            if(canSideLoad()){
                return;
            }else{
                requestSideLoad();
            }
        }
        public boolean canSideLoad(){
            try {
                int installNonMarketApps = Secure.getInt(context.getContentResolver(), Secure.INSTALL_NON_MARKET_APPS);
                Logger.logd( "installNonMarketApps: " + installNonMarketApps, verbose, System.out);
                return 1== installNonMarketApps;
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }
        public void requestSideLoad(){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.unknown_sources_title);
            builder.setMessage(R.string.unknown_sources_text);
            builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
                }
            });
            builder.show();
        }
    }

    public static boolean canAccessStorage(Context context){
        BuildFiles files = new BuildFiles(context);
        if(files.extApk.exists()){
            files.extApk.delete();
        }
        try{
            files.extApk.createNewFile();
            files.extApk.delete();
            return true;
        } catch (IOException e) {
            Logger.logd( files.extApk.getAbsolutePath(), verbose, System.out);
            return false;
        }
    }
    public static boolean hasEnoughSpace(Context context){
        BuildFiles files = new BuildFiles(context);
        try {
            files.extApk.createNewFile();
            long free = files.extApk.getFreeSpace();
            Logger.logd( "freespace: " + free, verbose, System.out);
            return free/1024 > 5;
        } catch (IOException e) {
            return false;
        }
    }
    
    static class TopDogCheck extends TimerTask {
        Timer timer;
        WeakReference<Activity> activityRef;
        int myTaskId;
        private static int interval = 1000;
        BuildFiles files;
        TopDogCheck(Timer timer, Activity activity){
            files = new BuildFiles(activity);
            this.timer = timer;
            activityRef = new WeakReference<Activity>(activity);
            ActivityManager activityManager = (ActivityManager) activity.getBaseContext().getSystemService(Activity.ACTIVITY_SERVICE);
            List<RunningTaskInfo> list = activityManager.getRunningTasks (1);
            myTaskId = list.get(0).id;
            Logger.logd("Self check started. Task id: " + myTaskId, verbose, System.out);
        }
        private TopDogCheck(Timer timer, Activity activity, int myTaskId){
            files = new BuildFiles(activity);
            this.timer = timer;
            this.myTaskId = myTaskId;
            activityRef = new WeakReference<Activity>(activity);
            ActivityManager activityManager = (ActivityManager) activity.getBaseContext().getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks (1);
            int topTaskId = list.get(0).id;
            Logger.logd("Still top dog: " + (myTaskId == topTaskId), verbose, System.out);
            if(myTaskId != topTaskId){
                files.deleteFiles(files.extApk);
                activityRef.clear();
            }
        }

        @Override
        public void run() {
            Activity activity = activityRef.get();
            if(activity!=null) {
                if(files.extApk.exists()) {
                    timer.schedule(new TopDogCheck(timer, activity, myTaskId), interval);
                    timer = null;
                }
            }
        }
    }
}
