package com.hjyd.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * @program：sfidworker
 * @description：异常类工具
 * @author：黄细初
 * @create：2019-05-24 09:41
 */
public class ExceptionUtils {


    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    private static final String CAUSE_CAPTION = "Caused by: ";


    /**
     * @Description：将异常(getStackTrace)转换为String
     * @Param：
     * @return：
     * @Author：黄细初
     * @Date：2019/5/24
     */
    public static String getStackMsg(Throwable e) {
        if (e == null) {
            return "";
        }
        String message = e.getMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("" + e.getClass().getTypeName() + ": " + (message == null ? "" : message) + "\n");
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append("\tat " + element.toString() + "\n");
        }
        return sb.toString();
    }


    public static String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<Throwable> dejaVu =
                Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(e);
        sb.append(e.toString() + "\n");
        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            sb.append("\tat " + traceElement + "\n");
        }

        // Print suppressed exceptions, if any
        for (Throwable se : e.getSuppressed()) {
            printEnclosedStackTrace(sb, trace, SUPPRESSED_CAPTION, "\t", dejaVu, se);
        }

        // Print cause, if any
        Throwable ourCause = e.getCause();
        if (ourCause != null) {
            printEnclosedStackTrace(sb, trace, CAUSE_CAPTION, "", dejaVu, ourCause);
        }
        return sb.toString();
    }

    public static void printEnclosedStackTrace(StringBuilder sb,
                                               StackTraceElement[] enclosingTrace,
                                               String caption,
                                               String prefix,
                                               Set<Throwable> dejaVu,
                                               Throwable thisThrowable) {
        if (dejaVu.contains(thisThrowable)) {
            sb.append("\t[CIRCULAR REFERENCE:" + thisThrowable + "]\n");
        } else {
            dejaVu.add(thisThrowable);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = thisThrowable.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            sb.append(prefix + caption + thisThrowable + "\n");
            for (int i = 0; i <= m; i++) {
                sb.append(prefix + "\tat " + trace[i] + "\n");
            }
            if (framesInCommon != 0) {
                sb.append(prefix + "\t... " + framesInCommon + " more\n");
            }

            // Print suppressed exceptions, if any
            for (Throwable se : thisThrowable.getSuppressed()) {
                printEnclosedStackTrace(sb, trace, SUPPRESSED_CAPTION,
                        prefix + "\t", dejaVu, se);
            }


            // Print cause, if any
            Throwable ourCause = thisThrowable.getCause();
            if (ourCause != null) {
                printEnclosedStackTrace(sb, trace, CAUSE_CAPTION, prefix, dejaVu, ourCause);
            }
        }
    }
}
