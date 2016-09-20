package com.theaetetuslabs.javaapkmaker;

import com.theaetetuslabs.javaapkmaker.ApkMaker.Callbacks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args){
        System.out.println("From apkmaker");
        List<String> argList = Arrays.asList(args);
        Iterator<String> i = argList.iterator();
        try {
            ApkMakerOptions options = new ApkMakerOptions();
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
        com.theaetetuslabs.javaapkmaker.ApkMaker apkMaker = new com.theaetetuslabs.javaapkmaker.ApkMaker(options, projectFiles, out, err);
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
    public static class ApkMakerOptions{
        public String aapt = null;
        public String androidJar ="android.jar";
        public String projectDir;
        public String outputFile;
        public boolean verbose = false;
        {
            if(File.separator.equals("/")){
                outputFile = "~/com.theaetetuslabs.apkmaker/newapk.apk";
            }else{
                outputFile = "%UserProfile%\\com.theaetetuslabs.apkmaker\\newapk.apk";
            }
        }
        void verifyOne(String path, String which) throws FileNotFoundException {
            if(!new File(path).exists()){
                throw new FileNotFoundException("Listed " + which + ", " + path + " not found");
            }
        }
        void verifyAll() throws FileNotFoundException {
            verifyOne(projectDir, "project directory");
            verifyOne(androidJar, "android.jar");
            if(aapt!=null){
                verifyOne(aapt, "aapt binary file");
            }
        }
    }
    static class ProjectFiles {
        String resDir;
        String javaDir;
        String assetsDir;
        String androidManifest;
        String outputDir;
        String apkName;
        public ProjectFiles(ApkMakerOptions options) throws Exception {
            setProjectDir(options.projectDir);
            setOutputDir(options.outputFile);
        }
        private String getFilePath(String label, File parent, String fileName) throws Exception {
            File file = new File(parent, fileName);
            if(!file.exists()){
                throw new Exception(label + " does not exist in " + parent.getAbsolutePath());
            }else{
                return file.getAbsolutePath();
            }
        }
        private void setProjectDir(String projectDir)throws Exception{
            File projectDirFile = new File(projectDir);
            androidManifest = getFilePath("AndroidManifest.xml", projectDirFile, "AndroidManifest.xml");
            resDir = getFilePath("Directory \"res\"", projectDirFile, "res");
            File assetsDirFile = new File(projectDirFile, "assets");
            assetsDir = assetsDirFile.exists() ? assetsDirFile.getAbsolutePath() : null;
            javaDir = getFilePath("Directory \"java\"", projectDirFile, "java");
        }
        private void setOutputDir(String outputFile) throws Exception {
            File outputFileFile = new File(outputFile);
            File outputDirFile = new File(outputFile).getParentFile();
            if(!outputDirFile.canWrite()){
                throw new Exception("Cannot write to output directory " + outputDirFile.getAbsolutePath());
            }
            outputDir = outputDirFile.getAbsolutePath();
            apkName = outputFileFile.getName();
        }
    }
    static void p(String str){
        System.out.println(str);
    }
}
