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
