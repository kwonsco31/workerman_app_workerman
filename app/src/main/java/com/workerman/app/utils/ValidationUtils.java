package com.workerman.app.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    /*휴대폰 유효성검사*/
    public static boolean isValidCellPhoneNumber(String cellphoneNumber) {

        boolean returnValue = false;


        String regex = "^\\s*(010|011|012|013|014|015|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

        Pattern p = Pattern.compile(regex);

        Matcher m = p.matcher(cellphoneNumber);

        if (m.matches()) {
            returnValue = true;
        }

        return returnValue;
    }

    /*일반전화 유효성 검사*/
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String strPhoneNumber = StringReplace(phoneNumber);
        boolean returnValue = false;
        String regex = "^\\s*(02|031|032|033|041|042|043|051|052|053|054|055|061|062|063|064|070)?(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(strPhoneNumber);
        if (m.matches()) {
            returnValue = true;
        }
        return returnValue;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /* 이메일 유효성 검사*/
    public static boolean isEmail(String email) {
        if (email==null) return false;
        boolean b = Pattern.matches("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+",email.trim());
        return b;
    }

    //비밀번호 유효성
    public static boolean isPassword(String _pw) {
//        if (!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", _pw)) {
        if (!Pattern.matches("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,}$", _pw)) {
            return true;
        }
//        1. 가장 많이 사용되는 최소 8자리에 숫자, 문자, 특수문자 각각 1개 이상 포함
//        "^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,}$"
//        2. 최소 8자리에 대문자 1자리 소문자 한자리 숫자 한자리
//        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$"
//        3. 최소 8자리에 대문자 1자리 소문자 1자리 숫자 1자리 특수문자 1자리
//        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&]{8,}"
//        4. 소문자 숫자 8자리 이상
//        "^(?=.*[a-z])(?=.*\\d)[a-zA-Z\\d]{8,}$"
//        "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,16}$";
        return false;
    }

    public static boolean isValidateDate(String _strDate){

        String strDate = StringReplace(_strDate);
        boolean result = false;
        SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyyMMdd");
        dateFormatParser.setLenient(false);
        try{
            dateFormatParser.parse(strDate);
            result = true;
        }catch(Exception Ex)
        {
            Ex.printStackTrace();
            result = false;
        }
        return result;
    }

    /*특수문자 제거*/
    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, " ");
        return str;
    }

}
