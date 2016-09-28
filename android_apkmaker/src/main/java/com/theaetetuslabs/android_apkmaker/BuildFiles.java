package com.theaetetuslabs.android_apkmaker;

import android.content.Context;
import android.os.AsyncTask;

import com.theaetetuslabs.java_apkmaker.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by bclinthall on 8/19/16.
 */
public class BuildFiles {
    private File extFileDir;
    File extApk;
    private File filesDir;
    private File buildDir;
    private File apks;
    File signed;
    File unsigned;
    File androidJar;
    File aapt;
    File initDir(File parent, String childName){
        File dir = new File(parent, childName);
        if(!dir.exists()) dir.mkdir();
        return dir;
    }
    BuildFiles(Context context){
        extFileDir = context.getExternalFilesDir(null);
        extApk = new File(extFileDir, "extApk.apk");
        filesDir = context.getFilesDir();
        buildDir = initDir(filesDir, "buildStuff");
        apks = initDir(buildDir, "apks");
        signed = new File(apks, "signed.apk");
        unsigned = new File(apks, "unsigned.apk");
        androidJar = new File(buildDir, "android.jar");
        aapt = new File(buildDir, "aapt");
    }
    BuildFiles clear(){
        deleteFiles(extApk, signed, unsigned);
        return this;
    }


    static void deleteFiles(File... files){
        //deleteFileLoud(files);
        new DeleteFileTask().execute(files);
    }
    private static void deleteFileLoud(File... files){
        for(File file : files){
            Logger.logd("isDirectory: " + file.isDirectory() + " exists: " + file.exists() + " deleted: " + file.delete() + file.getAbsolutePath(), true, System.out);
            try {
                File second = new File(file.getParentFile(), "second");
                Logger.logd("second" + second.getAbsolutePath(), true, System.out);
                second.createNewFile();
                second.delete();
            } catch (IOException e) {
                Logger.trace(e, System.err);
            }
        }
    }
    private static class DeleteFileTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            for(File file : params){
                file.delete();
            }
            return null;
        }
    }
}
