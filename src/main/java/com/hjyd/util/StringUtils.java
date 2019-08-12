package com.hjyd.util;

import com.hjyd.exception.ApplicationException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description：字符串工具类
 * @Author：黄细初
 * @Date：2019/5/24
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    public static final String NULL = "null";

    private static final String RAND_CHARS = "0123456789abcdefghigklmnopqrstuvtxyzABCDEFGHIGKLMNOPQRSTUVWXYZ";

    private static final String DIAN = ".";

    private static final Random RANDOM = new Random();

    private static Pattern PATTERN_CH = Pattern.compile("[\u4e00-\u9fa5]");

    public static String isoToGbk(String src) {
        if (src == null) {
            return null;
        }
        try {
            return new String(src.getBytes("ISO-8859-1"), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String isoToUtf(String src) {
        if (src == null) {
            return null;
        }
        try {
            return new String(src.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String gbkToUtf(String src) {
        if (src == null) {
            return null;
        }
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String utfToGbk(String src) {
        if (src == null) {
            return null;
        }
        try {
            return URLDecoder.decode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串前补零操作。
     *
     * @param src
     * @param length
     * @return
     */
    public static String patchZeroToString(String src, int length) {
        StringBuilder result = new StringBuilder("");
        int index = src.trim().length();
        if (index < length) {
            for (int i = (length - index); i > 0; i--) {
                result.append("0");
            }
        }
        result.append(src.trim());

        return result.toString();
    }

    /**
     * 字符串后补零操作。
     *
     * @param src
     * @param length
     * @return
     */
    public static String afterZeroToString(String src, int length) {
        StringBuilder result = new StringBuilder();
        int index = src.trim().length();
        result.append(src.trim());
        if (index < length) {
            for (int i = (length - index); i > 0; i--) {
                result.append("0");
            }
        }

        return result.toString();
    }

    /**
     * 货币金额小写转大写
     *
     * @param money 货币金额
     * @return
     */
    public static String moneyLToU(String money) {
        String result = "";
        StringBuffer item = new StringBuffer("");
        int index = 1;
        int len = 0;
        if (null == money) {
            throw new ApplicationException("The input param is null");
        }

        money = money.trim();
        if ("".equals(money)) {
            throw new ApplicationException("The input param is empty");
        }

        try {
            Float.parseFloat(money);
        } catch (NumberFormatException ex) {
            throw ex;
        }

        if (money.indexOf(DIAN) >= 0 && money.length() - 1 >= money.indexOf(DIAN) + 2) {
            money = money.substring(0, money.indexOf(".") + 3);
        } else if (money.indexOf(DIAN) >= 0 && money.length() - 1 < money.indexOf(DIAN) + 2) {
            money = money + "0";
        } else {
            money = money + ".00";
        }

        len = money.length();
        for (; index <= len; index++) {
            switch (money.substring(len - index, len - index + 1).charAt(0)) {
                case '.':
                    item.append("元");
                    break;
                case '0':
                    item.append("零");
                    break;
                case '1':
                    item.append("壹");
                    break;
                case '2':
                    item.append("贰");
                    break;
                case '3':
                    item.append("叁");
                    break;
                case '4':
                    item.append("肆");
                    break;
                case '5':
                    item.append("伍");
                    break;
                case '6':
                    item.append("陆");
                    break;
                case '7':
                    item.append("柒");
                    break;
                case '8':
                    item.append("捌");
                    break;
                case '9':
                    item.append("玖");
                    break;
                default:
                    break;
            }

            switch (index) {
                case 1:
                    item.append("分");
                    break;
                case 2:
                    item.append("角");
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    item.append("拾");
                    break;
                case 6:
                    item.append("佰");
                    break;
                case 7:
                    item.append("仟");
                    break;
                case 8:
                    item.append("万");
                    break;
                case 9:
                    item.append("拾");
                    break;
                case 10:
                    item.append("佰");
                    break;
                case 11:
                    item.append("仟");
                    break;
                case 12:
                    item.append("亿");
                    break;
                case 13:
                    item.append("拾");
                    break;
                case 14:
                    item.append("佰");
                    break;
                case 15:
                    item.append("仟");
                    break;
                case 16:
                    item.append("万");
                    break;
                default:
                    break;
            }
            result = item.toString() + result;
            item.delete(0, item.length());
        }
        result = result.replaceAll("零拾", "零");
        result = result.replaceAll("零佰", "零");
        result = result.replaceAll("零仟", "零");
        result = result.replaceAll("零零零", "零");
        result = result.replaceAll("零零", "零");
        result = result.replaceAll("零角零分", "整");
        result = result.replaceAll("零分", "整");
        result = result.replaceAll("零角", "零");
        result = result.replaceAll("零亿零万零元", "亿元");
        result = result.replaceAll("亿零万零元", "亿元");
        result = result.replaceAll("零亿零万", "亿");
        result = result.replaceAll("零万零元", "万元");
        result = result.replaceAll("万零元", "万元");
        result = result.replaceAll("零亿", "亿");
        result = result.replaceAll("零万", "万");
        result = result.replaceAll("零元", "元");
        result = result.replaceAll("零零", "零");
        if ("元".equals(result.substring(0, 1))) {
            result = result.substring(1);
        }
        if ("零".equals(result.substring(0, 1))) {
            result = result.substring(1);
        }
        if ("角".equals(result.substring(0, 1))) {
            result = result.substring(1);
        }
        if ("分".equals(result.substring(0, 1))) {
            result = result.substring(1);
        }
        if ("整".equals(result.substring(0, 1))) {
            result = "零元整";
        }
        return result;
    }

    public static String substring(String src, int length) {
        src = StringUtils.trimToEmpty(src);
        Integer size = src.length();
        if (size > length) {
            return src.substring(0, length) + "...";
        }
        return src;
    }

    public static String trimToEmpty(String src) {
        if (StringUtils.isEmpty(src)) {
            return EMPTY;
        } else {
            if (NULL.equals(StringUtils.lowerCase(src))) {
                return EMPTY;
            }
        }

        return StringUtils.trim(src);
    }

    public static String trim(String src, String defaultValue) {
        if (StringUtils.isEmpty(src)) {
            return defaultValue;
        } else {
            return StringUtils.trimToEmpty(src);
        }
    }


    /**
     * 将字符串数组转换成int数组
     *
     * @param strArr
     * @return
     */
    public static int[] stringArrToIntArr(String[] strArr) {
        int[] intArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            intArr[i] = Integer.valueOf(strArr[i]);
        }
        return intArr;
    }


    /**
     * 判断是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Matcher m = PATTERN_CH.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 获取随机字符
     *
     * @param length    随机字符长度
     * @param isOnlyNum 是否只是数字
     * @return 随机字符串
     */
    public static String getRandStr(int length, boolean isOnlyNum) {
        int size = isOnlyNum ? 10 : 62;
        StringBuffer hash = new StringBuffer(length);
//		Random random = new Random();
        for (int i = 0; i < length; i++) {
            hash.append(RAND_CHARS.charAt(RANDOM.nextInt(size)));
        }
        return hash.toString();
    }

    public static boolean nullOrBlank(String str) {
        return str == null || str.length() == 0;
    }

    public static String trimStart(String str, String replacement) {
        if (nullOrBlank(str) || !str.startsWith(replacement)) {
            return str;
        }
        return str.replaceFirst(replacement, "");
    }

    public static String trimEnd(String str, String replacement) {
        if (nullOrBlank(str) || !str.endsWith(replacement)) {
            return str;
        }
        int lastIndex = str.lastIndexOf(replacement);
        return str.substring(0, lastIndex);
    }



}
