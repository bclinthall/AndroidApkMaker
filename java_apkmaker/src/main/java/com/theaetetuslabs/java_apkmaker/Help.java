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

package com.theaetetuslabs.java_apkmaker;

/**
 * Created by bclinthall on 8/10/16.
 */
public class Help {
    public static final String help =
            "ApkMaker help:\n" +
            "ApkMaker will make an unsigned android apk for you. Here are the arguments it takes:\n" +
            "  First comes the path to the project directory.\n" +
            "    The project directory must be the immediate parent of an \n" +
            "    \"AndroidManifest.xml\" file, a \"res\" directory, and a \"java\" directory.\n" +
            "  Second comes the path to the android.jar file against which to compile the apk.\n\n" +
            "After the path to the android.jar file come the options, as follows.\n" +
            "  --verbose (or -v) print lots of debug information.\n" +
            "  --aapt (or -a) path to aapt executable.  If none is specified, then aapt must be in your path.\n" +
            "  --out (or -o) The path of the unsigned apk to be made. It must be in a writable directory. \n" +
            "      Defaults to \"~/com.theaetetuslabs.apkmaker/newapk.apk\" \n "+
            "      or \"%UserProfile%\\com.theaetetuslabs.apkmaker\\newapk.apk\".\n";


//  jarsigner -keystore ~/.android/debug.keystore -storepass android -keypass android unsigned.apk androiddebugkey
//  adb install unsigned.apk

    //can remove junit/\* android/rendscript/\* android/provider/\* android/test/\* org/apache/\*  android/media/\* android/webkit/\* android/hardware/\* android/drm/\* android/telephone/\* android/nfc/\* android/opengl/\* android/animation/\ android/accounts/\* android/speech/\* android/bluetooth/\* android/location/\* java/security/\* java/sql/\*
    //cannot remove java/util/\* android/net/\* java/io/\*

    //testing
}
