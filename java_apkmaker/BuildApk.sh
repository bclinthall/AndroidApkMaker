#!/bin/bash




#parameters needed to run java_apkbuilder
aapt=~/Android/Sdk/build-tools/24.0.2/aapt
androidJar=~/Android/Sdk/platforms/android-14/android.jar
app=./build/install/java_apkmaker/bin/java_apkmaker
projectDir=./BuildApk/sampleProject;
out=./BuildApk/unsigned.apk

#Unpack zip if no sampleProject found
if [ ! -d "$projectDir" ]; then
		printf "Project directory not found. Unpacking test.zip from ApkMakerTester assets."
		mkdir $projectDir;
		unzip ../apkmaker_tester/src/main/assets/test.zip -d $projectDir;
fi
	


#parameters needed to sign apk
keystore=./BuildApk/debug.keystore
keyAlias=androiddebugkey

#run java_apkmaker
$app $projectDir $androidJar -v --aapt $aapt --out $out

#sign apk
printf "\n\n***The password for the debug key is \"android\".  Enter it where prompted below if you are using the debug keystore.\n"
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore $keystore $out $keyAlias
mv $out ./BuildApk/signed.apk 

#This should leave you with a signed apk, ready to install.
