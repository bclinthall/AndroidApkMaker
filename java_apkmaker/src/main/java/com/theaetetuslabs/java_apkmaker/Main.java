package com.theaetetuslabs.java_apkmaker;


import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.theaetetuslabs.java_apkmaker.ApkMaker.ApkMakerOptions;
import com.theaetetuslabs.java_apkmaker.ApkMaker.Callbacks;
import com.theaetetuslabs.java_apkmaker.ApkMaker.ProjectFiles;



public class Main {
    public static void main(String[] args){
        System.out.println("From apkmaker");
        List<String> argList = Arrays.asList(args);
        Iterator<String> i = argList.iterator();
        try {
            final ApkMaker.ApkMakerOptions options = new ApkMakerOptions();
            options.projectDir = getNecArg("project directory", i);
            options.androidJar = getNecArg("android.jar", i);
            while (i.hasNext()) {
                readOption(i, options);
            }
            main(options);
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.err.print(Help.help);
            return;
        }

    }
    public static void main(ApkMakerOptions options){
        main(options, null, null, new Callbacks() {
            @Override
            public void updateProgress(String msg, float percent) {
                System.out.print(msg + percent + "%");
                System.out.println();
            }

            @Override
            public void error(String str) {
                System.err.print(str);
                System.err.println();
            }

            @Override
            public void done(File apk) {
                System.out.println("done");
            }
        });

    }
    public static void main(ApkMakerOptions options, PrintStream out, PrintStream err, Callbacks callbacks){
        if(out==null) out = System.out;
        if(err==null) err = System.err;

        try{
            options.verifyAll();
            ProjectFiles projectFiles = new ProjectFiles(options);
            makeApk(options, projectFiles, out, err, callbacks);
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.err.print(Help.help);
            return;
        }
    }
    private static void makeApk(ApkMakerOptions options, ProjectFiles projectFiles, PrintStream out, PrintStream err, Callbacks callbacks){
        ApkMaker apkMaker = new ApkMaker(options, projectFiles, out, err);
        apkMaker.makeApk(callbacks);
    }
    private static String getNecArg(String which, Iterator<String> i) throws Exception {
        if(i.hasNext()){
            String nxt = i.next();
            if(!nxt.startsWith("-")) {
                return nxt;
            }else{
                throw new Exception("Listed "+which+", " + nxt + " should not begin with \"-\"");
            }
        }else{
            throw new Exception("No "+which+" listed.");
        }
    }
    private static void readOption(Iterator<String> i, ApkMakerOptions options) throws Exception {
        String nxt = i.next();
        if(nxt.equals("-o") || nxt.equals("--out")){
            options.outputFile = i.next();
        }else if(nxt.equals("-a") || nxt.equals("--aapt")){
            options.aapt = i.next();
        }else if(nxt.equals("-v")||nxt.equals("--verbose")){
            options.verbose = true;
        }else{
            throw new Exception("Unknown option \""+nxt+"\"");
        }
    }

    static void p(String str){
        System.out.println(str);
    }
}
