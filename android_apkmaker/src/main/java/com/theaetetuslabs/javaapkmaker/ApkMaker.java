package com.theaetetuslabs.javaapkmaker;

import com.android.sdklib.build.ApkCreationException;
import com.android.sdklib.build.DuplicateFileException;
import com.android.sdklib.build.SealedApkException;
import com.android.sdklib.internal.build.SignedJarBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bclinthall on 4/15/16.
 */
public class ApkMaker {
    Main.ApkMakerOptions options;
    Main.ProjectFiles projectFiles;
    boolean verbose;
    PrintStream out = System.out;
    PrintStream err = System.err;
    public ApkMaker(Main.ApkMakerOptions options, Main.ProjectFiles projectFiles, PrintStream out, PrintStream err){
        this(options, projectFiles);
        this.out = out;
        this.err = err;
    }
    public ApkMaker(Main.ApkMakerOptions options, Main.ProjectFiles projectFiles){
        this.options = options;
        this.projectFiles = projectFiles;
        //verbose = options.verbose;
        verbose = true;//I don't know why, but without the debug statements, the program fails.
    }
    public void makeApk(Callbacks callbacks) {
        //needed directories;
        File temp = new File(projectFiles.outputDir, projectFiles.apkName + "_tmp");
        temp.mkdir();
        File classesDir = new File(temp, "classes");
        classesDir.mkdir();
        //File to be made
        File resourcesArsc = freshFile(temp, "resources.arsc");
        File classesDex = freshFile(temp, "classes.dex");
        File apkUnsigned = new File(options.outputFile);


        //Files passed in
        File aapt = options.aapt == null ? null : new File(options.aapt);
        File androidJar = new File(options.androidJar);
        File androidManifest = new File(projectFiles.androidManifest);
        File resDir = new File(projectFiles.resDir);
        File assetsDir = projectFiles.assetsDir == null ? null : new File(projectFiles.assetsDir);
        File srcDir = new File(projectFiles.javaDir);

        callbacks.updateProgress("Running aapt", -1);
        //File rJava = new File(srcDir, "R.java");
        runAapt(aapt, androidManifest, resourcesArsc, androidJar, resDir, srcDir/* genDir*/, callbacks);
        //if (rJava.exists()) {
        //    javaFiles.add(rJava);
        //}

        callbacks.updateProgress("Compiling", -1);

        runCompiler(srcDir, androidJar, classesDir);

        callbacks.updateProgress("Dexing, Building and Signing Apk, Final Steps", -1);
        try {
            runDex(classesDex, classesDir);
        } catch (IOException e) {
            trace(e);
            callbacks.error("IOException. Unable to dex files.");
            deleteRecursive(temp);
            return;
        }

        makeApk(apkUnsigned, resourcesArsc, classesDex, assetsDir, callbacks);

        if (!apkUnsigned.exists()) {
            callbacks.error("Unable to build apk.");
            deleteRecursive(temp);
            return;
        }
        deleteRecursive(temp);
        callbacks.done(apkUnsigned);
    }

    private void runAapt(File aapt, File androidManifest, File resourcesArsc, File androidJar, File resDir, File genDir, Callbacks callbacks) {
        //run aapt
        //./aapt p -f -v -M precursors/AndroidManifest.xml -F precursors/resources.arsc -I android.jar -S precursors/res -J precursors/gen
        String aaptCommand = aapt.getAbsolutePath() + " package " +
                " -f " + //force overwrite existing files;
                " -v " +//verbose
                " -M " + androidManifest.getAbsolutePath() +
                " -F " + resourcesArsc.getAbsolutePath() +  //this is where aapt will output the resource file to go in apk
                " -I " + androidJar.getAbsolutePath() +
                " -S " + resDir.getAbsolutePath() +
                " -J " + genDir.getAbsolutePath();  //where to put R.java
        //exec(aaptCommand, callbacks, totAaptLines, "Running aapt");
        exec(aaptCommand);
    }

    private void runCompiler(File srcDir,/*,File genDir, */File androidJar, File classesDir/*, List<File> javaFiles*/) {
        String[] compilerArgs = {
                "-classpath", srcDir.getAbsolutePath() +/* ":" + genDir.getAbsolutePath() + */":" + classesDir.getAbsolutePath(),
                "-verbose",
                "-bootclasspath", androidJar.getAbsolutePath(),
                "-1.6",  //java version to use
                "-target", "1.6", //target java level
                "-proc:none", //perform compilation but do not run annotation processors
                // for some reason we get an error if we try to run annotation processors:
                // java.lang.NoClassDefFoundError: org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl
                "-d", classesDir.getAbsolutePath(), //where the result will be put - makes folders to match package path
                srcDir.getAbsolutePath()
        };

        logd( "Compiling", verbose);
        boolean systemExitWhenFinished = false;//
        org.eclipse.jdt.internal.compiler.batch.Main compiler = new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(out), new PrintWriter(err), systemExitWhenFinished, null, null);
        if (compiler.compile(compilerArgs)) {
            logd( "Compile Success", verbose);
        }else{
            logd("Compile Error", verbose);
        }

    }

    private void runDex(File classesDex, File classesDir) throws IOException {
        //run dex //for this one it's important that you be inside precursors/classes so the path to MainActivity.class matches the package name in the file.
        //java -cp /Users/bclinthall/AndroidStudioProjects/IconEffects/app/src/main/assets/dx.jar  com.android.dx.command.dexer.Main --output ../../precursors/MainActivity.dex com/theaetetuslabs/charlie/MainActivity.class
        //it might be best to run this as commandline
        String[] dexArgs = //"java -cp " + dx.getAbsolutePath() + "com.android.dx.command.dexer.Main" +
                {"--output", classesDex.getAbsolutePath(),
                        classesDir.getAbsolutePath()};
        logd( "Dexing", verbose);
        com.android.dx.command.dexer.Main.main(dexArgs);

    }

    private void makeApk(File apkUnsigned, File resourcesArsc, File classesDex, File assetsDir, Callbacks callbacks) {

        //run ApkBuilder
        //java -cp sdklib.jar com.android.sdklib.build.ApkBuilderMain iconpack.apk -v -u -z precursors/resources.arsc -f precursors/classes.dex
        logd( "Building Apk", verbose);

        try {
            com.android.sdklib.build.ApkBuilder apkBuilder = new com.android.sdklib.build.ApkBuilder(apkUnsigned, resourcesArsc, classesDex, null, out);
            if(assetsDir!=null) {
                File[] assetFiles = assetsDir.listFiles();
                for (File assetFile : assetFiles) {
                    apkBuilder.addFile(assetFile, "assets/" + assetFile.getName());
                }
            }
            apkBuilder.sealApk();
        } catch (ApkCreationException e) {
            callbacks.error("Unable to make apk: " + e.getMessage());
        } catch (SealedApkException e) {
            callbacks.error("Unable to make apk: " + e.getMessage());
        } catch (DuplicateFileException e) {
            e.printStackTrace();
        }

/*        com.android.sdklib.build.ApkBuilderMain.main(new String[]{
                apkUnsigned.getAbsolutePath(),
                "-v",
                "-u",
                "-z", resourcesArsc.getAbsolutePath(),
                "-f", classesDex.getAbsolutePath()
        });*/

    }


    

    //StackOverflow: http://codereview.stackexchange.com/questions/8835/java-most-compact-way-to-print-inputstream-to-system-out
    public long copyStream(InputStream is, OutputStream os) {
        final int BUFFER_SIZE = 8192;

        byte[] buf = new byte[BUFFER_SIZE];
        long total = 0;
        int len = 0;
        try {
            while (-1 != (len = is.read(buf))) {
                os.write(buf, 0, len);
                total += len;
            }
        } catch (IOException ioe) {
            throw new RuntimeException("error reading stream", ioe);
        }
        return total;
    }

    private void readStream(String msg, InputStream stream) throws IOException {
        if(verbose) {
            out.append(msg);
            copyStream(stream, out);
        }
    }

    int iPercent = 0;

    private void readStream(String msg, InputStream stream, Callbacks callbacks, int totalLines, String text) throws IOException {
        if(verbose) {
            out.println(msg);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        IOException exception = null;
        float lineCt = 0;
        iPercent = 0;
        try {
            do {
                line = reader.readLine();
                if(verbose) {
                    out.println(line);
                }
                lineCt++;
                float percent = lineCt / totalLines;
                int newIPercent = (int) (100 * lineCt / totalLines);
                if (newIPercent != iPercent) {
                    callbacks.updateProgress(text, percent);
                    iPercent = newIPercent;
                }
            } while (line != null);
        } catch (IOException e) {
            exception = e;
        } finally {
            reader.close();
        }
        if (exception != null) {
            throw exception;
        }


    }

    private void readStreams(Process pp, String name) {
        if(verbose) {
            try {
                readStream("------InputStream, " + name + "\n", pp.getInputStream());
                readStream("------ErrorStream, " + name + "\n", pp.getErrorStream());
                pp.waitFor();
            } catch (IOException e) {
                trace(e);
            } catch (InterruptedException e) {
                trace(e);
            }
        }
    }

    private void readStreams(Process pp, String name, Callbacks callbacks, int totalLines, String text) {
            try {
                readStream("------InputStream, " + name + "\n", pp.getInputStream(), callbacks, totalLines, text);
                readStream("------ErrorStream, " + name + "\n", pp.getErrorStream());
                pp.waitFor();
            } catch (IOException e) {
                trace(e);
            } catch (InterruptedException e) {
                trace(e);
            }
    }

    public void exec(String command) {
        logd( "exec: " + command, verbose);
        try {
            Process pp = Runtime.getRuntime().exec(command);
            readStreams(pp, command);
        } catch (Exception e) {
            trace(e);
        }
    }

    private void exec(String command, Callbacks callbacks, int totalLines, String text) {
        logd( "exec: " + text + command, verbose);
        try {
            Process pp = Runtime.getRuntime().exec(command);
            readStreams(pp, command, callbacks, totalLines, text);
        } catch (Exception e) {
            trace(e);
        }
    }

    private File freshFile(File dir, String name) {
        File f = new File(dir, name);
        if (f.exists()) {
            deleteRecursive(f);
        }
        return f;
    }

    private void deleteRecursive(File file){
        if(file.isDirectory()){
            for(File f : file.listFiles()){
                deleteRecursive(f);
            }
        }else{
            file.delete();
        }
    }
    void trace(Exception e){
        Logger.trace(e, err);
    }
    void logd(String msg, boolean verbose){
        Logger.logd(msg, verbose, out);
    }


    public interface Callbacks {
        void updateProgress(String msg, float percent);

        void error(String str);

        void done(File apk);
    }
}
