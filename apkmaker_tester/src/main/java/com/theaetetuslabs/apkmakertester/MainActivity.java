package com.theaetetuslabs.apkmakertester;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /*
    For testing android_apkmaker.  Included in the assests directory of this project is a test.zip file.
    You may replace it with your own, as you see fit.  test.zip should include an AndroidManifest.xml,
    a res directory, a java directory, and (optionally) an assets directory.
    APP_PACKAGE_NAME should be set to the package name specified in the AndroidManifest.xml in test.zip.
     */
    public static final String APP_PACKAGE_NAME = "com.theaetetuslabs.helloworld";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void buildAndInstall(View view){
        Intent intent = new Intent(this, ApkMakerService.class);
        startService(intent);
    }
}
