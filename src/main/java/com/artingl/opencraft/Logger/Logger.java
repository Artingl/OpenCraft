package com.artingl.opencraft.Logger;

import com.artingl.opencraft.OpenCraft;
import com.artingl.opencraft.Utils.StackTrace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String[] ALL_ANSI = {
            ANSI_RESET,
            ANSI_BLACK,
            ANSI_RED,
            ANSI_GREEN,
            ANSI_YELLOW,
            ANSI_BLUE,
            ANSI_PURPLE,
            ANSI_CYAN,
            ANSI_WHITE
    };

    protected static FileOutputStream outputFile;
    protected static PrintStream interceptor;
    protected static PrintStream origOut;

    public static void init() {
        origOut = System.out;
        interceptor = new Interceptor(origOut);
        System.setOut(interceptor);
    }

    public static void setupOutputFile() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh-mma z");
        outputFile = new FileOutputStream(OpenCraft.getGameDirectory() + "logs/" + formatter.format(ZonedDateTime.now()) + ".txt");
        outputFile.flush();
    }
    public static void debug(String msg) {
        debug(msg, true);
    }

    public static void info(String msg) {
        info(msg, true);
    }

    public static void error(String msg) {
        error(msg, true);
    }

    public static void exception(String msg, Exception e) {
        exception(msg, e, true);
    }

    public static void debug(String msg, boolean outputToFile) {
        writeToFile(String.format("%s[%s/DEBUG] (%s)%s %s%s\n", ANSI_CYAN, Calendar.getInstance().getTime(), getCallerClassName(), ANSI_YELLOW, msg,ANSI_RESET), outputToFile);
    }

    public static void info(String msg, boolean outputToFile) {
        writeToFile(String.format("%s[%s/INFO] (%s)%s %s%s\n", ANSI_CYAN, Calendar.getInstance().getTime(), getCallerClassName(), ANSI_WHITE, msg,ANSI_RESET), outputToFile);
    }

    public static void error(String msg, boolean outputToFile) {
        writeToFile(String.format("%s[%s/ERROR] (%s)%s %s%s\n", ANSI_CYAN, Calendar.getInstance().getTime(), getCallerClassName(), ANSI_RED, msg,ANSI_RESET), outputToFile);
    }

    public static void exception(String msg, Exception e, boolean outputToFile) {
        writeToFile(String.format("%s[%s/EXCEPTION] (%s)%s %s%s\n", ANSI_CYAN, Calendar.getInstance().getTime(), getCallerClassName(), ANSI_RED, msg,ANSI_RESET), outputToFile);

        for (String line: StackTrace.getStackTrace(e).split("\n")) {
            writeToFile("\t" + line + "\n", outputToFile);
        }
    }

    public static void closeOutput() {
        if (outputFile == null) return;

        try {
            outputFile.close();
        } catch (Exception e) {
            exception("Error while closing log file", e, false);
        }
    }

    protected static void writeToFile(String value, boolean outputToFile) {
        origOut.print(value);
        if (!outputToFile || outputFile == null) return;

        try {
            for (String s: ALL_ANSI)
                value = value.replace(s, "");
            byte[] buffer = value.getBytes();
            outputFile.write(buffer, 0, buffer.length);
            outputFile.flush();
        } catch (Exception e) {
            exception("Error writing to log file", e, false);
        }
    }

    protected static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Logger.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }

    public static class Interceptor extends PrintStream
    {
        public Interceptor(OutputStream out)
        {
            super(out, true);
        }

        public void print(String x)
        {
            Logger.info(x);
        }

        public void println(String x) {
            Logger.info(x);
        }

        public void print(boolean x) {
            print(String.valueOf(x));
        }

        public void print(char x) {

            print(String.valueOf(x));
        }

        public void print(int x) {

            print(String.valueOf(x));
        }

        public void print(long x) {

            print(String.valueOf(x));
        }

        public void print(float x) {

            print(String.valueOf(x));
        }

        public void print(double x) {

            print(String.valueOf(x));
        }

        public void print(char[] x) {

            print(String.valueOf(x));
        }

        public void print(Object x) {
            print(String.valueOf(x));
        }

        public void println(boolean x) {
            println(String.valueOf(x));
        }

        public void println(char x) {
            println(String.valueOf(x));
        }

        public void println(int x) {
            println(String.valueOf(x));
        }

        public void println(long x) {
            println(String.valueOf(x));
        }

        public void println(float x) {
            println(String.valueOf(x));
        }

        public void println(double x) {
            println(String.valueOf(x));
        }

        public void println(char[] x) {
            println(String.valueOf(x));
        }

        public void println(Object x) {
            println(String.valueOf(x));
        }
    }

}
