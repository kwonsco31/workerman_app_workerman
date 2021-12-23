package com.workerman.app.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.workerman.app.Defines;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

// android:debuggable="true" 일 경우에만 Log 찍힘. 

public class Debug {
	public static boolean isDebug = Defines.isDebuggable();

	final static Object obj = new Object();

	private static long start;
	private static long end;

	//private static Logger debugLogger = null;

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, attachCaller(msg));
	}

	public static void i(String tag, String msg, Throwable e) {
		if (isDebug)
			Log.i(tag, attachCaller(msg), e);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.v(tag, attachCaller(msg));
	}

	public static void V(String tag, String msg) {
		if (isDebug)
			Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (isDebug)
			Log.w(tag, attachCaller(msg));
	}

	public static void w(String tag, Throwable e) {
		if (isDebug)
			Log.w(tag, e);
	}

	public static void w(String tag, String msg, Throwable e) {
		if (isDebug)
			Log.w(tag, attachCaller(msg), e);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, attachCaller(msg));
	}

	public static void e(String tag, String msg, Throwable e) {
		if (isDebug)
			Log.e(tag, attachCaller(msg), e);
	}

	public static void d(String tag, String msg, Throwable e) {
		if (isDebug)
			Log.d(tag, attachCaller(msg), e);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, attachCaller(msg));
	}

	private static String attachCaller(String msg) {
		return msg + " | at " + getCurFunction();
	}

	private static String getCurFunction() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement lmnt = stackTrace[5];

		return lmnt.toString();
	}

	@TargetApi(11)
	public static String toString(Cursor cursor) {
		if (Build.VERSION.SDK_INT < 11)
			return "Requires API level 11 (current is " + Build.VERSION.SDK_INT
					+ ")";

		StringBuilder sb = new StringBuilder("{");
		for (int i = 0, l = cursor.getColumnCount(); i < l; i++) {
			sb.append(cursor.getColumnName(i));

			switch (cursor.getType(i)) {
			case Cursor.FIELD_TYPE_NULL:
				sb.append("=(NULL)");
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				sb.append(String.format("=(INTEGER)\"%d\"", cursor.getInt(i)));
				break;
			case Cursor.FIELD_TYPE_FLOAT:
				sb.append(String.format("=(FLOAT)\"%f\"", cursor.getFloat(i)));
				break;
			case Cursor.FIELD_TYPE_STRING:
				sb.append(String.format("=(STRING)\"%s\"", cursor.getString(i)));
				break;
			case Cursor.FIELD_TYPE_BLOB:
				sb.append(String.format("=(BLOB)\"%s\"", cursor.getBlob(i)));
				break;
			}

			if (i < l - 1)
				sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

	public static void start() {
		if (isDebug)
			start = System.currentTimeMillis();
	}

	public static void elapsedTime(String str) {
		if (isDebug)
			end = System.currentTimeMillis();
		i(str, "duration : " + (end - start));
		start = end;
	}

	public static void startNano() {
		if (isDebug)
			start = System.nanoTime();
	}

	public static void elapsedTimeNano(String str) {
		if (isDebug)
			end = System.nanoTime();
		i(str, "duration : " + (end - start));
		start = end;
	}

	@SuppressLint("SimpleDateFormat")
	public static void writeLog(String str) {
		String logPath = Environment.getExternalStorageDirectory()
		    .getAbsolutePath() + "/download/privia_log.txt";
		File file = new File(logPath);
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return;
			}
		} 

		{
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(logPath,true));
				long time = System.currentTimeMillis();
				SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				bw.write(dayTime.format(new Date(time)));
				bw.write("  ===  ");
				bw.write(str);
				bw.write("\r\n");
				bw.flush();
				bw.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	 }
}
