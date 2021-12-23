package com.workerman.app.utils;

import android.content.Context;
import android.util.Log;

import com.workerman.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by bada on 2017-12-12.
 */

public class DateUtil {
    private static final String TAG = "DateUtil";

    /*yymmdd 형태로 입력된 날짜를 xx월xx일 (요일) 형태로 반환합니다.*/
    public static String getStrToDayDate(Context _context, String _date){
        String result = "";
        try{
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat returnFormat = new SimpleDateFormat(_context.getString(R.string.date_format_type_a));
            Date destDate = destFormat.parse(_date);
            result = returnFormat.format(destDate);

        }catch (Exception e){
            Log.e(TAG, "error : " + e.getMessage());
        }
        return result;
    }

    /*yyyymm 형태로 입력된 날짜를 yyyy년 xx월 형태로 반환합니다.*/
    public static String getStrToDayDate2(Context _context, String _date){
        String result = "";
        try{
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyyMM");
            SimpleDateFormat returnFormat = new SimpleDateFormat(_context.getString(R.string.date_format_type_b));
            Date destDate = destFormat.parse(_date);
            result = returnFormat.format(destDate);

        }catch (Exception e){
            Log.e(TAG, "error : " + e.getMessage());
        }
        return result;
    }

    /*unixtimestamp값을 이용해 오전/오후 00:00으로 시간값을 반환*/
    public static String getUnixTimeToStrDate(Context _context, String _timestamp){
        String result_data = "";
        try {
            long timestamp = Long.parseLong(_timestamp);
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

//            if(Utils.getLanguageSet(_context).equals(Locale.US.getLanguage())){
//                dateFormat = new SimpleDateFormat("hh:mm a");
//            }

            result_data = dateFormat.format(date);
        }catch (Exception e){
            Debug.e(TAG, "getUnixTimeToStrDate error : " + e.getMessage());
        }

        return result_data;
    }
    public static String getUnixTimeToStrDate(Context _context, long _timestamp){
        String result_data = "";
        try {
            long timestamp = _timestamp;
            Date date = new Date(timestamp * 1000L);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

//            if(Utils.getLanguageSet(_context).equals(Locale.US.getLanguage())){
//                dateFormat = new SimpleDateFormat("hh:mm a");
//            }

            result_data = dateFormat.format(date);
        }catch (Exception e){
            Debug.e(TAG, "getUnixTimeToStrDate error : " + e.getMessage());
        }

        return result_data;
    }

    public static String getUnixTimeToStrDate(long _timestamp){
        String result_data = "";
        try {
            long timestamp = _timestamp;

            Date date = new Date(timestamp);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

//            if(Utils.getLanguageSet(_context).equals(Locale.US.getLanguage())){
//                dateFormat = new SimpleDateFormat("hh:mm a");
//            }

            result_data = dateFormat.format(date);
        }catch (Exception e){
            Debug.e(TAG, "getUnixTimeToStrDate error : " + e.getMessage());
        }

        return result_data;
    }

    /*total time값을 xx시간 xx분으로 반환*/
    public static String getCommuteWorkingTime(Context _context, String _total_time){
        String result = "";
        try{
            int total_time = Integer.parseInt(_total_time);

            int p1 = total_time % 60;
            int p2 = total_time / 60;
            int p3 = p2 % 60;

            result = String.format(_context.getString(R.string.time_format_type_a), p2, p3);
        }catch (Exception e){
            Debug.e(TAG, "getCommuteWorkingTime error: " + e.getMessage());
        }

        return result;
    }
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    private static String timeConversion(Context _context, int totalSeconds) {
        int hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        int minutes = (totalSeconds - (hoursToSeconds(hours))) / SECONDS_IN_A_MINUTE;
        int seconds = totalSeconds - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        return hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }

    public static String timeConversion(Context _context, String strTotalSeconds) {

        int totalSeconds = Integer.parseInt(strTotalSeconds);

        int hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        int minutes = (totalSeconds - (hoursToSeconds(hours))) / SECONDS_IN_A_MINUTE;
        int seconds = totalSeconds - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));
        if(hours > 0) {
            return String.format(_context.getString(R.string.time_format_type_a), hours, minutes);
        }else{
            return String.format(_context.getString(R.string.time_format_type_b), minutes);
        }
//        return hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }

    private static int hoursToSeconds(int hours) {
        return hours * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE;
    }

    private static int minutesToSeconds(int minutes) {
        return minutes * SECONDS_IN_A_MINUTE;
    }


    /*yyyyMM형태의 날짜를 주거나 _date가 공백일 경우, 현재 월을 yyyy.MM의 형태로 반환하고, 전달된 yyyy.MM의 형태의 날짜가 있으면 빼거나 더해서 반환한다.*/
    public static String getYearMonth(String _date, int increase){
        String result = "";
        try{
            Date date;
            GregorianCalendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
            if("".equals(_date)){
                date = new Date();
                result = dateFormat.format(date);
            }else{
                date = dateFormat.parse(_date);
                cal.setTime(date);
                cal.add(Calendar.MONTH, increase);
                date = cal.getTime();
                result = dateFormat.format(date);
            }
        }catch (Exception e){
            Debug.e(TAG, "getYearMonth error: "+e.getMessage());
        }
        return result;
    }

    public static String getYear(String _date, int increase){
        String result = "";
        try{
            Date date;
            GregorianCalendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
            if("".equals(_date)){
                date = new Date();
                result = dateFormat.format(date);
            }else{
                date = dateFormat.parse(_date);
                cal.setTime(date);
                cal.add(Calendar.YEAR, increase);
                date = cal.getTime();
                result = dateFormat.format(date);
            }
        }catch (Exception e){
            Debug.e(TAG, "getYearMonth error: "+e.getMessage());
        }
        return result;
    }

    /*public static String getStrDateToSimpleFormat(Context _context, String _strDate) {
        String result_data = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat returnFormat = new SimpleDateFormat("a hh:mm");

            if(Utils.getLanguageSet(_context).equals(Locale.US.getLanguage())){
                returnFormat = new SimpleDateFormat("hh:mm a");
            }

            if (_strDate.length() < 21) {
                _strDate = _strDate.substring(0, 21);
            }

            Date destDate = destFormat.parse(_strDate);
            result_data = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result_data;
    }*/

    public static String getStrDateToSimpleFormat2(String _strDate) {
        String result = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            if (_strDate.length() < 21) {
                _strDate = _strDate.substring(0, 21);
            }

            Date destDate = destFormat.parse(_strDate);
            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat4(String _strDate) {
        String result = "";
        try {
//            Aug 19, 2018 9:30:04 AM
//            Aug 21, 2018 9:44:31 PM Aug 21, 2018 9:44:31 PM
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss aaa", Locale.ENGLISH);
            SimpleDateFormat returnFormat = new SimpleDateFormat("MM-dd");

            Date destDate = destFormat.parse(_strDate);

            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat5(String _strDate) {
        String result = "";
        try {
//            Aug 19, 2018 9:30:04 AM
//            Aug 21, 2018 9:44:31 PM Aug 21, 2018 9:44:31 PM
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss aaa", Locale.ENGLISH);
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

            Date destDate = destFormat.parse(_strDate);

            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat3(String _strDate) {
        String result = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date destDate = destFormat.parse(_strDate);
            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat6(String _strDate) {
        String result = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy.MM.dd");

            Date destDate = destFormat.parse(_strDate);
            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat7(String _strDate) {
        String result = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy.MM.dd");

            Date destDate = destFormat.parse(_strDate);
            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat8(String _strDate) {
        String result = "";
        try {
//            Aug 19, 2018 9:30:04 AM
//            Aug 21, 2018 9:44:31 PM Aug 21, 2018 9:44:31 PM
            SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, yyyy h:mm:ss aaa", Locale.ENGLISH);
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date destDate = destFormat.parse(_strDate);

            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getStrDateToSimpleFormat9(String _strDate) {
        String result = "";
        try {
            SimpleDateFormat destFormat = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat returnFormat = new SimpleDateFormat("yyyy.MM.dd. HH:mm");

            Date destDate = destFormat.parse(_strDate);
            result = returnFormat.format(destDate);
        } catch (Exception e) {
            Debug.e(TAG, "getStrDateToSimpleFormat error : " + e.getMessage());
        }

        return result;
    }

    public static String getNowTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getJustTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getNowDay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getNowDay2(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Date getStrToDate( String _strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date resultDate = null;
        try {
            resultDate = dateFormat.parse(_strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public static Date getStrToDate2( String _strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date resultDate = null;
        try {
            resultDate = dateFormat.parse(_strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public static String getStrToNextDate( String _strDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date resultDate = null;
        try {
            resultDate = dateFormat.parse(_strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(resultDate);
        cal.add(Calendar.DAY_OF_YEAR, 1);

        String strDate = dateFormat.format(cal.getTime());

        return strDate;
    }

    public static String getStrToNextDateTime( String _strDate){
        //한시간 경과후의 시간
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date resultDate = null;
        try {
            resultDate = dateFormat.parse(_strDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(resultDate);
        cal.add(Calendar.HOUR_OF_DAY, 1);

        String strDate = dateFormat.format(cal.getTime());

        return strDate;
    }
}
