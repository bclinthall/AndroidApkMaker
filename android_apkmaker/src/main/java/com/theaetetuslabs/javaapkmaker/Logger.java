package com.theaetetuslabs.javaapkmaker;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by bclinthall on 8/19/16.
 */
public class Logger {

    public static void trace(Exception e, boolean verbose, PrintStream err){
        if(verbose) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            err.print(errors.toString());
        }
    }
    public static void trace(Exception e, PrintStream err){
        trace(e, true, err);
    }
    public static void logd(String msg, boolean verbose, PrintStream out){
        if(verbose){
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            out.println(msg + " -- " + stack[2].toString());
        }
    }
    public static void logd2(String msg, boolean verbose, PrintStream out){
        if(verbose){
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            out.println( "<<<<<<<<<<");
            out.println( msg );
            for(int i=0; i<stack.length; i++){
                out.println( " -- "+i+": " + stack[i].toString());
            }
            out.println( ">>>>>>>>>>");
        }
    }
}
