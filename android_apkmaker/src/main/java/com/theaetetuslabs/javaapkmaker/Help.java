package com.theaetetuslabs.javaapkmaker;

/**
 * Created by bclinthall on 8/10/16.
 */
public class Help {
    public static final String help =
            "ApkMaker help:\n" +
            "ApkMaker will make an unsigned android apk for you. Here are the arguments it takes:" +
            "First comes the path to the project directory.  The project directory must be the immediate parent of an \"AndroidManifest.xml\" file, a \"res\" directory, and a \"java\" directory.\n" +
            "Second comes the path to the android.jar file against which to compile the apk.\n" +
            "After the path to the android.jar file come the options, as follows.\n" +
            "--verbose (or -v) print lots of debug information.\n" +
            "--out (or -o) The path of the unsigned apk to be made. It must be in a writable directory. " +
                    "Defaults to \"~/com.theaetetuslabs.apkmaker/newapk.apk\" or \"%UserProfile%\\com.theaetetuslabs.apkmaker\\newapk.apk\"." +
            "--aapt (or -a) path to aapt executable.  If none is specified, then aapt must be in your path.";


//  jarsigner -keystore ~/.android/debug.keystore -storepass android -keypass android unsigned.apk androiddebugkey
//  adb install unsigned.apk

    //can remove junit/\* android/rendscript/\* android/provider/\* android/test/\* org/apache/\*  android/media/\* android/webkit/\* android/hardware/\* android/drm/\* android/telephone/\* android/nfc/\* android/opengl/\* android/animation/\ android/accounts/\* android/speech/\* android/bluetooth/\* android/location/\* java/security/\* java/sql/\*
    //cannot remove java/util/\* android/net/\* java/io/\*

    //testing
}
