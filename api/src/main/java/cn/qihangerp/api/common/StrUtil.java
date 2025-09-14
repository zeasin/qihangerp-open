package cn.qihangerp.api.common;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StrUtil {
    public static boolean isBlank(CharSequence str) {
        int length;
        if (str != null && (length = str.length()) != 0) {
            for(int i = 0; i < length; ++i) {
                if (!CharUtil.isBlankChar(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }
    /**
     * 将字符串转换为驼峰命名法。
     *
     * @param str 原始字符串
     * @param separator 分隔符（如 '-'、'_' 等）
     * @return 驼峰命名法的字符串
     */
    public static String toCamelCase(String str, char separator) {
        // 如果输入为空，直接返回空字符串
        if (str == null || str.isEmpty()) {
            return str;
        }

        // 将字符串分割成多个单词
        String[] words = str.split(String.valueOf(separator));

        // 第一个单词保持小写，其余单词首字母大写
        StringBuilder camelCaseString = new StringBuilder(words[0].toLowerCase());

        // 处理后续的单词，将首字母大写并拼接到结果字符串
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            // 将首字母转为大写，其余小写
            camelCaseString.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }

        return camelCaseString.toString();
    }

    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (!isEmpty(str) && !isEmpty(prefix)) {
            String str2 = str.toString();
            return str2.startsWith(prefix.toString()) ? subSuf(str2, prefix.length()) : str2;
        } else {
            return str(str);
        }
    }
    public static String subSuf(CharSequence string, int fromIndex) {
        return isEmpty(string) ? null : sub(string, fromIndex, string.length());
    }
    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        } else {
            int len = str.length();
            if (fromIndexInclude < 0) {
                fromIndexInclude += len;
                if (fromIndexInclude < 0) {
                    fromIndexInclude = 0;
                }
            } else if (fromIndexInclude > len) {
                fromIndexInclude = len;
            }

            if (toIndexExclude < 0) {
                toIndexExclude += len;
                if (toIndexExclude < 0) {
                    toIndexExclude = len;
                }
            } else if (toIndexExclude > len) {
                toIndexExclude = len;
            }

            if (toIndexExclude < fromIndexInclude) {
                int tmp = fromIndexInclude;
                fromIndexInclude = toIndexExclude;
                toIndexExclude = tmp;
            }

            return fromIndexInclude == toIndexExclude ? "" : str.toString().substring(fromIndexInclude, toIndexExclude);
        }
    }

    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        } else if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof byte[]) {
            return str((byte[])obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[])obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer)obj, charset);
        } else {
            return ArrayUtil.isArray(obj) ? ArrayUtil.toString(obj) : obj.toString();
        }
    }
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return "null";
        } else {
            return !ArrayUtil.isEmpty(params) && !isBlank(template) ? StrFormatter.format(template.toString(), params) : template.toString();
        }
    }
}
