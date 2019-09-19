package com.veni.tools.util;

import android.os.Build;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by kkan on 2016/1/24.
 * 数据处理相关
 * isNullString                : 判断字符串是否为空 为空即true
 * isEmpty                     : 判断对象是否为空 为空即true
 * isNumber                    : 判断字符串是否是数字
 * isNumeric                   : 判断是否全是数字 (正整数和负整数)
 * isMobile                    : 验证手机号（精确）
 * isTel                       : 验证电话号码
 * isBankCard                  : 验证银卡卡号
 * isIdCard                    : 验证15位和18位身份证号码
 * isEmail                     : 验证邮箱
 * isURL                       : 验证URL
 * isChz                       : 验证汉字
 * isUsername                  : 验证用户名
 * isDate                      : 验证yyyy-MM-dd格式的日期校验，已考虑平闰年
 * isIP                        : 验证IP地址
 * checkPostcode               : 匹配中国邮政编码
 * isMatch                     : string是否匹配regex
 * ------------------------
 * getBigDecimalnum            : 数字 四舍五入
 * hideMobilePhone4            : 隐藏手机中间4位号码
 * formatCard                  : 格式化银行卡
 * bytes2HexString             : byteArr转hexString
 * hex2Dec                     : hexChar转int
 * byte2FitSize                : 字节数转合适大小
 * getPercentValue             : 获取百分比
 */

public class DataUtils {
    /*存储相关常量*/
    private static final int BYTE = 1;//Byte
    private static final int KB = 1024;//KB与Byte的倍数
    private static final int MB = 1048576;//MB与Byte的倍数
    private static final int GB = 1073741824;//GB与Byte的倍数
    private static final Integer FIFTEEN_ID_CARD = 15;//15位身份证号
    private static final Integer EIGHTEEN_ID_CARD = 18;//18位身份证号

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 判断字符串是否为空 为空即true
     *
     * @param str 字符串
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    public static boolean isNullString(@Nullable String str) {
        return str == null || str.length() == 0 || "null".equals(str);
    }

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return {@code true}: 为空<br>{@code false}: 不为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        }
        if (obj instanceof SparseArray && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseBooleanArray && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if (obj instanceof SparseIntArray && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (obj instanceof SparseLongArray) && ((SparseLongArray) obj).size() == 0;
        }
        return false;
    }

    /**
     * 判断string 是否为数字
     * double AppBarStateChangeListener = -19162431.1254
     * 当数字位数很长时，系统会自动转为科学计数法。所以aa=-1.91624311254E7.
     *
     * @param numstr 待验证文本
     * @return {@code true}: 为数字<br>{@code false}: 不为数字
     */
    public static boolean isNumber(String numstr) {
        String numRegex = "-?[0-9]+\\.?[0-9]*";
        return isMatch(numRegex, numstr);
    }

    /**
     * 判断是否全是数字 (正整数和负整数)
     *
     * @param str 待验证文本
     * @return {@code true}: 为数字<br>{@code false}: 不为数字
     */
    public static boolean isNumeric(String str) {
        String numRegex = "-?[0-9]*";
        return isMatch(numRegex, str);
    }


    /**
     * 正则：手机号（精确）
     * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * 联通：130、131、132、145、155、156、175、176、185、186
     * 电信：133、153、173、177、180、181、189
     * 全球星：1349
     * 虚拟运营商：170
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobile(String string) {
        String regex = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
        return isMatch(regex, string);
    }

    /**
     * 验证电话号码
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isTel(String string) {
        String regex = "^0\\d{2,3}[- ]?\\d{7,8}";
        return isMatch(regex, string);
    }

    /**
     * 比较真实完整的判断身份证号码的工具
     *
     * @param IdCard 用户输入的身份证号码
     * @return [true符合规范, false不符合规范]
     */
    public static boolean isRealIDCard(String IdCard) {
        if (IdCard != null) {
            int correct = new IdCardUtils(IdCard).isCorrect();
            if (0 == correct) {// 符合规范
                return true;
            }
        }
        return false;
    }
    /**
     * 验证身份证号码15或18位 包含以x结尾
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard(String string) {
        String regex = "(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|x|X)$)";
        return isMatch(regex, string);
    }

    /**
     * 根据身份证号获取性别
     */
    public static String getSex(String IDCard) {
        String sex = "";
        if (isEmpty(IDCard) || !isRealIDCard(IDCard)) {
            return sex;
        }
        //15位身份证号
        if (IDCard.length() == FIFTEEN_ID_CARD) {
            if (Integer.parseInt(IDCard.substring(14, 15)) % 2 == 0) {
                sex = "女";
            } else {
                sex = "男";
            }
            //18位身份证号
        } else if (IDCard.length() == EIGHTEEN_ID_CARD) {
            // 判断性别
            if (Integer.parseInt(IDCard.substring(16).substring(0, 1)) % 2 == 0) {
                sex = "女";
            } else {
                sex = "男";
            }
        }
        return sex;
    }

    /**
     * 根据身份证号获取年龄
     */
    public static int getAge(String IDCard) {
        int age = 0;
        if (isEmpty(IDCard) || !isRealIDCard(IDCard)) {
            return age;
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //15位身份证号
        if (IDCard.length() == FIFTEEN_ID_CARD) {
            // 身份证上的年份(15位身份证为1980年前的)
            String uyear = "19" + IDCard.substring(6, 8);
            // 身份证上的月份
            String uyue = IDCard.substring(8, 10);
            // 当前年份
            String fyear = format.format(date).substring(0, 4);
            // 当前月份
            String fyue = format.format(date).substring(5, 7);
            if (Integer.parseInt(uyue) <= Integer.parseInt(fyue)) {
                age = Integer.parseInt(fyear) - Integer.parseInt(uyear) + 1;
                // 当前用户还没过生
            } else {
                age = Integer.parseInt(fyear) - Integer.parseInt(uyear);
            }
            //18位身份证号
        } else if (IDCard.length() == EIGHTEEN_ID_CARD) {
            // 身份证上的年份
            String year = IDCard.substring(6).substring(0, 4);
            // 身份证上的月份
            String yue = IDCard.substring(10).substring(0, 2);
            // 当前年份
            String fyear = format.format(date).substring(0, 4);
            // 当前月份
            String fyue = format.format(date).substring(5, 7);
            // 当前月份大于用户出身的月份表示已过生日
            if (Integer.parseInt(yue) <= Integer.parseInt(fyue)) {
                age = Integer.parseInt(fyear) - Integer.parseInt(year) + 1;
                // 当前用户还没过生日
            } else {
                age = Integer.parseInt(fyear) - Integer.parseInt(year);
            }
        }
        return age;
    }

    /**
     * 获取出生日期  yyyy年MM月dd日
     */
    public static String getBirthday(String IDCard, String dataFormat) {
        String birthday = "";
        if (isEmpty(IDCard) || !isRealIDCard(IDCard)) {
            return birthday;
        }
        String year = "";
        String month = "";
        String day = "";
        //15位身份证号
        if (IDCard.length() == FIFTEEN_ID_CARD) {
            // 身份证上的年份(15位身份证为1980年前的)
            year = "19" + IDCard.substring(6, 8);
            //身份证上的月份
            month = IDCard.substring(8, 10);
            //身份证上的日期
            day = IDCard.substring(10, 12);
            //18位身份证号
        } else if (IDCard.length() == EIGHTEEN_ID_CARD) {
            // 身份证上的年份
            year = IDCard.substring(6).substring(0, 4);
            // 身份证上的月份
            month = IDCard.substring(10).substring(0, 2);
            //身份证上的日期
            day = IDCard.substring(12).substring(0, 2);
        }
        birthday = year + "年" + month + "月" + day + "日";
        if (dataFormat.equals(TimeUtils.dateFormatYMDofChinese)) {
            return birthday;
        }
        birthday = TimeUtils.formatDate(birthday, TimeUtils.dateFormatYMDofChinese, dataFormat);
        return birthday;
    }

    /**
     * 验证邮箱
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmail(String string) {
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return isMatch(regex, string);
    }

    /**
     * 验证URL
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isURL(String string) {
        String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        return isMatch(regex, string);
    }

    /**
     * 验证中文标点
     * <p>
     * //匹配这些中文标点符号 。 ？ ！ ， 、 ； ： “ ” ‘ ' （ ） 《 》 〈 〉 【 】 『 』 「 」 ﹃ ﹄ 〔 〕 … — ～ ﹏ ￥
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isChPunctuation(String string) {
        String regex = "[\\u3002|\\uff1f|\\uff01|\\uff0c|\\u3001|\\uff1b|\\uff1a|\\u201c|\\u201d|\\u2018|\\u2019|\\uff08|\\uff09|\\u300a|\\u300b|\\u3008|\\u3009|\\u3010|\\u3011|\\u300e|\\u300f|\\u300c|\\u300d|\\ufe43|\\ufe44|\\u3014|\\u3015|\\u2026|\\u2014|\\uff5e|\\ufe4f|\\uffe5]+";
        return isMatch(regex, string);
    }

    /**
     * 验证标点 (包含中英文)
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isPunctuation(String string) {
        String regex = "[\\p{P}]+";
        return isMatch(regex, string);
    }

    /**
     * 验证大写英文
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isCapitalsEn(String string) {
        String regex = "[A-Z]+";
        return isMatch(regex, string);
    }

    /**
     * 验证小写英文及英文标点
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isLowerEn(String string) {
        String regex = "[a-z]+";
        return isMatch(regex, string);
    }

    /**
     * 验证汉字
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isChz(String string) {
        String regex = "^[\\u4e00-\\u9fa5]+$";
        return isMatch(regex, string);
    }

    /**
     * 验证用户名
     * <p>取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是x-xx位</p>
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isUsername(String string, String namebeginlength, String nameendlength) {
        String regex = "^[\\w\\u4e00-\\u9fa5]{" + namebeginlength + "," + nameendlength + "}(?<!_)$";
        return isMatch(regex, string);
    }

    /**
     * 验证yyyy-MM-dd格式的日期校验，已考虑平闰年
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isDate(String string) {
        String regex = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
        return isMatch(regex, string);
    }

    /**
     * 验证IP地址
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIP(String string) {
        String regex = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
        return isMatch(regex, string);
    }


    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean checkPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return isMatch(regex, postcode);
    }

    /**
     * string是否匹配regex正则表达式字符串
     *
     * @param regex  正则表达式字符串
     * @param string 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String regex, String string) {
        return !isNullString(string) && Pattern.matches(regex, string);
    }

    /**
     * 四舍五入 保留2位小数
     *
     * @param value 数值
     * @return String
     */
    public static String getBigDecimalnum(String value) {
        return getBigDecimalnum(value, 2);
    }

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return String
     */
    public static String getBigDecimalnum(String value, int digit) {
        if (!isNumber(value)) {
            return "0";
        }
        return getBigDecimalnum(Double.parseDouble(value), digit);
    }

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return String
     */
    public static String getBigDecimalnum(double value, int digit) {
        BigDecimal result = BigDecimal.valueOf(value);
        return getBigDecimalnum(result, digit);
    }

    /**
     * 四舍五入
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return String
     */
    private static String getBigDecimalnum(BigDecimal value, int digit) {
        return value.setScale(digit, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 隐藏手机中间4位号码
     * 130****0000
     *
     * @param mobile_phone 手机号码
     * @return 130****0000
     */
    public static String hideMobilePhone4(String mobile_phone) {
        if (mobile_phone.length() != 11) {
            return "手机号码不正确";
        }
        return mobile_phone.substring(0, 3) + "****" + mobile_phone.substring(7, 11);
    }

    /**
     * 格式化银行卡 加*
     * 3749 **** **** 330
     *
     * @param cardNo 银行卡
     * @return 3749 **** **** 330
     */
    public static String formatCard(String cardNo) {
        if (cardNo.length() < 8) {
            return "银行卡号有误";
        }
        String card = "";
        card = cardNo.substring(0, 4) + " **** **** ";
        card += cardNo.substring(cardNo.length() - 4);
        return card;
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes byte数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        char[] ret = new char[bytes.length << 1];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * hexString转byteArr
     * <p>例如：</p>
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String hexString) {
        int len = hexString.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >>> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    /**
     * hexChar转int
     *
     * @param hexChar hex单个字节
     * @return 0..15
     */
    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 字节数转合适大小
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 1...1024 unit
     */
    public static String byte2FitSize(long byteNum) {
        if (byteNum < 0) {
            return "0.00KB";
        } else if (byteNum < KB) {
            return String.format(Locale.getDefault(), "%.3fB", (double) byteNum);
        } else if (byteNum < MB) {
            return String.format(Locale.getDefault(), "%.3fKB", (double) byteNum / KB);
        } else if (byteNum < GB) {
            return String.format(Locale.getDefault(), "%.3fMB", (double) byteNum / MB);
        } else {
            return String.format(Locale.getDefault(), "%.3fGB", (double) byteNum / GB);
        }
    }


    /**
     * 获取百分比（乘100）
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return String
     */
    public static String getPercentValue(BigDecimal value, int digit) {
        BigDecimal result = value.multiply(BigDecimal.valueOf(100));
        return getBigDecimalnum(result, digit);
    }

    /**
     * 获取百分比（乘100）
     *
     * @param value 数值
     * @param digit 保留小数位
     * @return String
     */
    public static String getPercentValue(double value, int digit) {
        BigDecimal result = BigDecimal.valueOf(value);
        return getPercentValue(result, digit);
    }

    /**
     * 获取百分比（乘100,保留两位小数）
     *
     * @param value 数值
     * @return String
     */
    public static String getPercentValue(double value) {
        return getPercentValue(value, 2);
    }
}
