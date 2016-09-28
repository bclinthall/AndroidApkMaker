#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <android/log.h>
#include "aapt/jni/Main.cpp"

#define DEBUG_TAG "JavaIDEdroid"

// any underscore you use in a package/method name must
// be translated into _1 on the native side.
// So, com.theaetetuslabs.android_apkmaker becomes com_theaetetuslabs_android_1apkmaker

//===================================================================
jint Java_com_theaetetuslabs_android_1apkmaker_TArnAapt_JNImain(JNIEnv * env, jobject this, jstring args)
//===================================================================
{
	jboolean isCopy;
	const char * szArgs = (*env)->GetStringUTFChars(env, args, &isCopy);
  char *ptr1, *ptr2;
	int i, idx, argc=1, len;
	jint rc = 99;
	
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "Native method call: JNImain (%s)", szArgs);
	len=strlen(szArgs);
	for (i=0; i<len; i++) if (szArgs[i]=='\t') argc++;
	char * argv[argc];
	ptr1 = ptr2 = (char*) szArgs;
	idx = 0;
	for (i=0; i<len; i++)
	{
	  if (*ptr2=='\t') 
	  {
	    *ptr2=0;
	    argv[idx]=ptr1;
	    idx++;
	    ptr1=ptr2+1;
	  }
	  ptr2++;
	} // for
  argv[idx]=ptr1;
	
	// redirect stderr and stdout
  freopen ("/sdcard/.JavaIDEdroid/native_stderr.txt", "w", stderr);
  freopen ("/sdcard/.JavaIDEdroid/native_stdout.txt", "w", stdout);
  
  fprintf (stdout, "Aapt arguments:\n");
  for (i=1; i<argc; i++) fprintf (stdout, "%s\n",argv[i]);
  
  // call aapt
	rc = main (argc, argv);

  // stopping the redirection
  fclose (stderr);
  fclose (stdout);
	
	return rc;
} // JNImain
//===================================================================
