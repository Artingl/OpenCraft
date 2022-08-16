package com.artingl.opencraft.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTrace {

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
