com.theaetetuslabs.com.theaetetuslabs.javaapkmaker.Main can be run from command line.

If building on an android device, you'll need to include the android.jar file against which you want to compile in the assests directory.  Can be found in the directory where you have your android sdk.  

<path-to-android-sdk>/platforms/android-<version you want to compile against>/android.jar

If you know what will be included in the apk you want to compile, you might be able to remove some of the files from android.jar to save space and still compile successfully.
