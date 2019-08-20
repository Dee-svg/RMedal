package com.veni.tools;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by kkan on 2016/1/24.
 * Log管理
 */
public class LogUtils {
    @IntDef({LogLevel.V, LogLevel.D, LogLevel.I,
            LogLevel.W, LogLevel.E, LogLevel.A,
            LogLevel.FILE_I, LogLevel.FILE, LogLevel.JSON_I,
            LogLevel.JSON, LogLevel.XML_I, LogLevel.XML})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogLevel {
        int V = 2;
        int D = 3;
        int I = 4;
        int W = 5;
        int E = 6;
        int A = 7;
        int FILE_I = 16;
        int FILE = 19;
        int JSON_I = 32;
        int JSON = 35;
        int XML_I = 48;
        int XML = 51;
    }

    private static final char[] T = new char[]{'V', 'D', 'I', 'W', 'E', 'A'};
    private static ExecutorService sExecutor;
    private static String sDefaultDir;
    private static String sDir;
    private static String sFilePrefix = "util";
    private static boolean sLogSwitch = true;
    private static boolean sLog2ConsoleSwitch = true;
    private static String sGlobalTag = null;
    private static boolean sTagIsSpace = true;
    private static boolean sLogHeadSwitch = true;
    private static List<Integer> leave_no_head_list = Arrays.asList(LogLevel.V, LogLevel.D, LogLevel.I);//在集合里的日志不打印头部
    private static boolean sLog2FileSwitch = false;
    private static boolean sLogBorderSwitch = true;
    private static int sConsoleFilter = 2;
    private static int sFileFilter = 2;
    private static int sStackDeep = 1;
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String TOP_BORDER = "╔═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final String SPLIT_BORDER = "╟───────────────────────────────────────────────────────────────────────────────────────────────────";
    private static final String LEFT_BORDER = "║ ";
    private static final String BOTTOM_BORDER = "╚═══════════════════════════════════════════════════════════════════════════════════════════════════";
    private static final int MAX_LEN = 4000;
    private static final Format FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault());
    private static final String NULL = "null";
    private static final String ARGS = "args";
    private static final LogUtils.Config CONFIG = new LogUtils.Config();

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static LogUtils.Config getConfig() {
        return CONFIG;
    }

    public static void v(Object... contents) {
        vTag(sGlobalTag, contents);
    }

    public static void vTag(String tag, Object... contents) {
        log(LogLevel.V, tag, contents);
    }

    public static void d(Object... contents) {
        dTag(sGlobalTag, contents);
    }

    public static void dTag(String tag, Object... contents) {
        log(LogLevel.D, tag, contents);
    }

    public static void i(Object... contents) {
        iTag(sGlobalTag, contents);
    }

    public static void iTag(String tag, Object... contents) {
        log(LogLevel.I, tag, contents);
    }

    public static void w(Object... contents) {
        wTag(sGlobalTag, contents);
    }

    public static void wTag(String tag, Object... contents) {
        log(LogLevel.W, tag, contents);
    }

    public static void e(Object... contents) {
        eTag(sGlobalTag, contents);
    }

    public static void eTag(String tag, Object... contents) {
        log(LogLevel.E, tag, contents);
    }

    public static void a(Object... contents) {
        aTag(sGlobalTag, contents);
    }

    public static void aTag(String tag, Object... contents) {
        log(LogLevel.A, tag, contents);
    }

    public static void file(Object content) {
        file(sGlobalTag, content);
    }

    public static void file(int type, Object content) {
        file(type, sGlobalTag, content);
    }

    public static void file(String tag, Object content) {
        log(LogLevel.FILE, tag, content);
    }

    public static void file(int type, String tag, Object content) {
        log(LogLevel.FILE_I| type, tag, content);
    }

    public static void json(String content) {
        json(sGlobalTag, content);
    }

    public static void json(int type, String content) {
        json(type, sGlobalTag, content);
    }

    public static void json(String tag, String content) {
        log(LogLevel.JSON, tag, content);
    }

    public static void json(int type, String tag, String content) {
        log(LogLevel.JSON_I | type, tag, content);
    }

    public static void xml(String content) {
        xml(sGlobalTag, content);
    }

    public static void xml(int type, String content) {
        xml(type, sGlobalTag, content);
    }

    public static void xml(String tag, String content) {
        log(LogLevel.XML, tag, content);
    }

    public static void xml(int type, String tag, String content) {
        log(LogLevel.XML_I | type, tag, content);
    }

    private static void log(int type, String tag, Object... contents) {
        if (sLogSwitch && (sLog2ConsoleSwitch || sLog2FileSwitch)) {
            int type_low = type & 15;
            int type_high = type & 240;
            if (type_low >= sConsoleFilter || type_low >= sFileFilter) {
                LogUtils.TagHead tagHead = processTagAndHead(type, tag);
                String body = processBody(type_high, contents);
                if (sLog2ConsoleSwitch && type_low >= sConsoleFilter && type_high != 16) {
                    print2Console(type_low, tagHead.tag, tagHead.consoleHead, body);
                }

                if ((sLog2FileSwitch || type_high == 16) && type_low >= sFileFilter) {
                    print2File(type_low, tagHead.tag, tagHead.fileHead + body);
                }

            }
        }
    }

    private static LogUtils.TagHead processTagAndHead(int type, String tag) {
        if (!sTagIsSpace && !sLogHeadSwitch) {
            tag = sGlobalTag;
        } else {
            StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
            StackTraceElement targetElement = stackTrace[3];
            String fileName = targetElement.getFileName();
            String className;
            if (fileName == null) {
                className = targetElement.getClassName();
                String[] classNameInfo = className.split("\\.");
                if (classNameInfo.length > 0) {
                    className = classNameInfo[classNameInfo.length - 1];
                }

                int index = className.indexOf(36);
                if (index != -1) {
                    className = className.substring(0, index);
                }

                fileName = className + ".java";
            } else {
                int index = fileName.indexOf(46);
                className = index == -1 ? fileName : fileName.substring(0, index);
            }

            if (sTagIsSpace) {
                tag = isSpace(tag) ? className : tag;
            }

            if (sLogHeadSwitch && !leave_no_head_list.contains(type)) {
                String tName = Thread.currentThread().getName();
                String head = (new Formatter()).format("%s, %s(%s:%d)", tName, targetElement.getMethodName(), fileName, targetElement.getLineNumber()).toString();
                String fileHead = " [" + head + "]: ";
                if (sStackDeep <= 1) {
                    return new LogUtils.TagHead(tag, new String[]{head}, fileHead);
                }

                String[] consoleHead = new String[Math.min(sStackDeep, stackTrace.length - 3)];
                consoleHead[0] = head;
                int spaceLen = tName.length() + 2;
                String space = (new Formatter()).format("%" + spaceLen + "s", "").toString();
                int i = 1;

                for (int len = consoleHead.length; i < len; ++i) {
                    targetElement = stackTrace[i + 3];
                    consoleHead[i] = (new Formatter()).format("%s%s(%s:%d)", space, targetElement.getMethodName(), targetElement.getFileName(), targetElement.getLineNumber()).toString();
                }

                return new LogUtils.TagHead(tag, consoleHead, fileHead);
            }
        }

        return new LogUtils.TagHead(tag, (String[]) null, ": ");
    }

    private static String processBody(int type, Object... contents) {
        String body = NULL;
        if (contents != null) {
            if (contents.length == 1) {
                Object object = contents[0];
                if (object != null) {
                    body = object.toString();
                }

                if (type == 32) {
                    body = formatJson(body);
                } else if (type == 48) {
                    body = formatXml(body);
                }
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 0;

                for (int len = contents.length; i < len; ++i) {
                    Object content = contents[i];
                    sb.append(ARGS).append("[").append(i).append("]").append(" = ").append(content == null ? NULL : content.toString()).append(LINE_SEP);
                }

                body = sb.toString();
            }
        }

        return body;
    }

    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = (new JSONObject(json)).toString(4);
            } else if (json.startsWith("[")) {
                json = (new JSONArray(json)).toString(4);
            }
        } catch (JSONException var2) {
            var2.printStackTrace();
        }

        return json;
    }

    private static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + LINE_SEP);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return xml;
    }

    private static void print2Console(int type, String tag, String[] head, String msg) {
        printBorder(type, tag, true);
        printHead(type, tag, head);
        printMsg(type, tag, msg);
        printBorder(type, tag, false);
    }

    private static void printBorder(int type, String tag, boolean isTop) {
        if (sLogBorderSwitch) {
            Log.println(type, tag, isTop ? TOP_BORDER : BOTTOM_BORDER);
        }

    }

    private static void printHead(int type, String tag, String[] head) {
        if (head != null) {
            String[] var3 = head;
            int var4 = head.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String aHead = var3[var5];
                Log.println(type, tag, sLogBorderSwitch ? LEFT_BORDER + aHead : aHead);
            }

            if (sLogBorderSwitch) {
                Log.println(type, tag, SPLIT_BORDER);
            }
        }

    }

    private static void printMsg(int type, String tag, String msg) {
        int len = msg.length();
        int countOfSub = len / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;

            for (int i = 0; i < countOfSub; ++i) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }

            if (index != len) {
                printSubMsg(type, tag, msg.substring(index, len));
            }
        } else {
            printSubMsg(type, tag, msg);
        }

    }

    private static void printSubMsg(int type, String tag, String msg) {
        if (!sLogBorderSwitch) {
            Log.println(type, tag, msg);
        } else {
            new StringBuilder();
            String[] lines = msg.split(LINE_SEP);
            String[] var5 = lines;
            int var6 = lines.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String line = var5[var7];
                Log.println(type, tag, LEFT_BORDER + line);
            }

        }
    }

    private static void print2File(int type, final String tag, String msg) {
        Date now = new Date(System.currentTimeMillis());
        String format = FORMAT.format(now);
        String date = format.substring(0, 5);
        String time = format.substring(6);
        final String fullPath = (sDir == null ? sDefaultDir : sDir) + sFilePrefix + "-" + date + ".txt";
        if (!createOrExistsFile(fullPath)) {
            Log.e(tag, "log to " + fullPath + " failed!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(time).append(T[type - 2]).append("/").append(tag).append(msg).append(LINE_SEP);
            final String content = sb.toString();
            execute(new Runnable() {
                public void run() {
                    BufferedWriter bw = null;

                    try {
                        bw = new BufferedWriter(new FileWriter(fullPath, true));
                        bw.write(content);
                        Log.d(tag, "log to " + fullPath + " success!");
                    } catch (IOException var11) {
                        var11.printStackTrace();
                        Log.e(tag, "log to " + fullPath + " failed!");
                    } finally {
                        try {
                            if (bw != null) {
                                bw.close();
                            }
                        } catch (IOException var10) {
                            var10.printStackTrace();
                        }

                    }

                }
            });
        }
    }

    private static boolean createOrExistsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        } else if (!createOrExistsDir(file.getParentFile())) {
            return false;
        } else {
            try {
                boolean r = file.createNewFile();
                printDeviceInfo(filePath);
                return r;
            } catch (IOException var3) {
                var3.printStackTrace();
                return false;
            }
        }
    }

    private static void printDeviceInfo(final String filePath) {
        String versionName = "";
        int versionCode = 0;

        try {
            PackageInfo pi = VnUtils.getApp().getPackageManager().getPackageInfo(VnUtils.getApp().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
        }

        final String head = "\n************* Log Head ****************\nDevice Manufacturer: " + Build.MANUFACTURER + "\nDevice Model       : " + Build.MODEL + "\nAndroid Version    : " + Build.VERSION.RELEASE + "\nAndroid SDK        : " + Build.VERSION.SDK_INT + "\nApp VersionName    : " + versionName + "\nApp VersionCode    : " + versionCode + "\n************* Log Head ****************\n\n";
        execute(new Runnable() {
            public void run() {
                BufferedWriter bw = null;

                try {
                    bw = new BufferedWriter(new FileWriter(filePath, true));
                    bw.write(head);
                } catch (IOException var11) {
                    var11.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException var10) {
                        var10.printStackTrace();
                    }

                }

            }
        });
    }

    private static boolean createOrExistsDir(File file) {
        boolean var10000;
        label25:
        {
            if (file != null) {
                if (file.exists()) {
                    if (file.isDirectory()) {
                        break label25;
                    }
                } else if (file.mkdirs()) {
                    break label25;
                }
            }

            var10000 = false;
            return var10000;
        }

        var10000 = true;
        return var10000;
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        } else {
            int i = 0;

            for (int len = s.length(); i < len; ++i) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static void execute(Runnable runnable) {
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }

        sExecutor.execute(runnable);
    }

    private static class TagHead {
        String tag;
        String[] consoleHead;
        String fileHead;

        TagHead(String tag, String[] consoleHead, String fileHead) {
            this.tag = tag;
            this.consoleHead = consoleHead;
            this.fileHead = fileHead;
        }
    }

    public static class Config {
        private Config() {
            if (LogUtils.sDefaultDir == null) {
                if ("mounted".equals(Environment.getExternalStorageState()) && VnUtils.getApp().getExternalCacheDir() != null) {
                    LogUtils.sDefaultDir = VnUtils.getApp().getExternalCacheDir() + LogUtils.FILE_SEP + "log" + LogUtils.FILE_SEP;
                } else {
                    LogUtils.sDefaultDir = VnUtils.getApp().getCacheDir() + LogUtils.FILE_SEP + "log" + LogUtils.FILE_SEP;
                }

            }
        }

        /**
         * @param logSwitch 允许输出日志开关{@code true}: 开<br>{@code false}: 关
         */
        public LogUtils.Config setLogSwitch(boolean logSwitch) {
            LogUtils.sLogSwitch = logSwitch;
            return this;
        }

        /**
         * @param consoleSwitch 允许开启控制台输出{@code true}: 开<br>{@code false}: 关
         */
        public LogUtils.Config setConsoleSwitch(boolean consoleSwitch) {
            LogUtils.sLog2ConsoleSwitch = consoleSwitch;
            return this;
        }

        /**
         * @param tag 设置Tag
         */
        public LogUtils.Config setGlobalTag(String tag) {
            if (LogUtils.isSpace(tag)) {
                LogUtils.sGlobalTag = "";
                LogUtils.sTagIsSpace = true;
            } else {
                LogUtils.sGlobalTag = tag;
                LogUtils.sTagIsSpace = false;
            }

            return this;
        }

        /**
         * @param logHeadSwitch 允许开启日志标题{@code true}: 开<br>{@code false}: 关
         */
        public LogUtils.Config setLogHeadSwitch(boolean logHeadSwitch, @LogLevel int... leave_nos) {
            LogUtils.sLogHeadSwitch = logHeadSwitch;
            if (leave_nos.length > 0) {
                LogUtils.leave_no_head_list = new ArrayList<>();
            }
            for (int leave_no : leave_nos) {
                LogUtils.leave_no_head_list.add(leave_no);
            }
            return this;
        }

        /**
         * @param log2FileSwitch 允许日志写入文件{@code true}: 开<br>{@code false}: 关
         */
        public LogUtils.Config setLog2FileSwitch(boolean log2FileSwitch) {
            LogUtils.sLog2FileSwitch = log2FileSwitch;
            return this;
        }

        /**
         * @param dir 设置写入的文件夹
         */
        public LogUtils.Config setDir(String dir) {
            if (LogUtils.isSpace(dir)) {
                LogUtils.sDir = null;
            } else {
                LogUtils.sDir = dir.endsWith(LogUtils.FILE_SEP) ? dir : dir + LogUtils.FILE_SEP;
            }

            return this;
        }

        /**
         * @param dir 设置写入的文件夹
         */
        public LogUtils.Config setDir(File dir) {
            LogUtils.sDir = dir == null ? null : dir.getAbsolutePath() + LogUtils.FILE_SEP;
            return this;
        }

        /**
         * @param filePrefix 设置写入文件夹的前缀
         */
        public LogUtils.Config setFilePrefix(String filePrefix) {
            if (LogUtils.isSpace(filePrefix)) {
                LogUtils.sFilePrefix = "util";
            } else {
                LogUtils.sFilePrefix = filePrefix;
            }

            return this;
        }

        /**
         * @param borderSwitch 允许开启日志边界{@code true}: 开<br>{@code false}: 关
         */
        public LogUtils.Config setBorderSwitch(boolean borderSwitch) {
            LogUtils.sLogBorderSwitch = borderSwitch;
            return this;
        }

        /**
         * @param consoleFilter 控制台
         */
        public LogUtils.Config setConsoleFilter(int consoleFilter) {
            LogUtils.sConsoleFilter = consoleFilter;
            return this;
        }

        public LogUtils.Config setFileFilter(int fileFilter) {
            LogUtils.sFileFilter = fileFilter;
            return this;
        }

        public LogUtils.Config setStackDeep(@IntRange(from = 1L) int stackDeep) {
            LogUtils.sStackDeep = stackDeep;
            return this;
        }

        public String toString() {
            return "switch: " + LogUtils.sLogSwitch + LogUtils.LINE_SEP + "console: " + LogUtils.sLog2ConsoleSwitch + LogUtils.LINE_SEP + "tag: " + (LogUtils.sTagIsSpace ? NULL : LogUtils.sGlobalTag) + LogUtils.LINE_SEP + "head: " + LogUtils.sLogHeadSwitch + LogUtils.LINE_SEP + "file: " + LogUtils.sLog2FileSwitch + LogUtils.LINE_SEP + "dir: " + (LogUtils.sDir == null ? LogUtils.sDefaultDir : LogUtils.sDir) + LogUtils.LINE_SEP + "filePrefix" + LogUtils.sFilePrefix + LogUtils.LINE_SEP + "border: " + LogUtils.sLogBorderSwitch + LogUtils.LINE_SEP + "consoleFilter: " + LogUtils.T[LogUtils.sConsoleFilter - 2] + LogUtils.LINE_SEP + "fileFilter: " + LogUtils.T[LogUtils.sFileFilter - 2] + LogUtils.LINE_SEP + "stackDeep: " + LogUtils.sStackDeep;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface TYPE {
    }
}
