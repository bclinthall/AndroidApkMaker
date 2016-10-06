This gradle project contains three modules.  

1. `java_apkmaker`  
  A java application that makes an unsigned android apk.  To use it as such, run `gradle :java_apkmaker:installDist`. The application will be installed at `java_apkmaker/build/install/java_apkmaker/` with runnables in `java_apkmaker/build/install/java_apkmaker/bin/`. See `java_apkmaker/src/main/java/com/theaetetuslabs/java_apkmaker/Help.java` for an help with using `java_apkmaker` from the command line. There is a script to run it at `java_apkmaker/BuildApk.sh` - modify paths as appropriate for your system.

  Relies on or includes
   * sdklib, copyright The Android Open Source Project, 2010, Apache 2.0 (see APACHE2.0.txt for full text).  
      An _extremely_ pared down version of sdklib is included here in the source.
   * ecj, copryright The Eclipse Foundation, 2015, Eclpise Public License Version 1.0 (EPL.txt for full text).  
      Included as a dependency in java_apkmaker/build.gradle
   * dx, copyright The Android Open Source Project, 2006, Apache 2.0 (see APACHE2.0.txt for full text).  
      Included as a dependency in java_apkmaker/build.gradle
   * aapt, copyright The Android Open Source Project, 2006, Apache 2.0 (see APACHE2.0.txt for full text).  
      Included as binary executables.
      Built from source modified by Tom Arn (https://github.com/t-arn/java-ide-droid/tree/master/jni), and B. Clint Hall (https://github.com/theaetetus/java-ide-droid/tree/master/jni)

   
2. `android_apkmaker`  
   An android library which relies on `java_apkmaker` to build an apk 
   To make this work, you need include the `android.jar` file against which you wish to compile in the `assets` directory.  So, for example, if the apk you wish to build will target android API 21, you need to use the Android SDK manager to download the SDK Platform for API 21.  You can then find `android.jar` in `<path-to-sdk>/platforms/android-21/`.  Copy that file to `android_apkmaker/src/main/assets/android.jar`.
   
   Relies on or includes the following jars.  All copyright Ken Ellingwood, 2010, licensed under Apache 2.0 (see APACHE2.0.txt for full text).  
   * kellinwood-logging-android-1.4.jar  
   * zipio-lib-1.8.jar  
   * kellinwood-logging-lib-1.1.jar  
   * zipsigner-lib-1.17.jar  
   * kellinwood-logging-log4j-1.0.jar  

3. `apkmaker_tester`  
   A simple android application for testing android_apkmaker.  Included in the assests directory of this project is a test.zip file.  You may replace it with your own, as you see fit.  test.zip should include an AndroidManifest.xml,
a res directory, a java directory, and (optionally) an assets directory.
   
   `MainActivity.APP_PACKAGE_NAME` should be set to the package name specified in the `AndroidManifest.xml` in `test.zip`.
