package cn.qihangerp.api.common;

public class StrFormatter {
    public static String format(String strPattern, Object... argArray) {
        return formatWith(strPattern, "{}", argArray);
    }

    public static String formatWith(String strPattern, String placeHolder, Object... argArray) {
        if (!StrUtil.isBlank(strPattern) && !StrUtil.isBlank(placeHolder) && !ArrayUtil.isEmpty(argArray)) {
            int strPatternLength = strPattern.length();
            int placeHolderLength = placeHolder.length();
            StringBuilder sbuf = new StringBuilder(strPatternLength + 50);
            int handledPosition = 0;

            for(int argIndex = 0; argIndex < argArray.length; ++argIndex) {
                int delimIndex = strPattern.indexOf(placeHolder, handledPosition);
                if (delimIndex == -1) {
                    if (handledPosition == 0) {
                        return strPattern;
                    }

                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }

                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == '\\') {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == '\\') {
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
                        handledPosition = delimIndex + placeHolderLength;
                    } else {
                        --argIndex;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(placeHolder.charAt(0));
                        handledPosition = delimIndex + 1;
                    }
                } else {
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(StrUtil.utf8Str(argArray[argIndex]));
                    handledPosition = delimIndex + placeHolderLength;
                }
            }

            sbuf.append(strPattern, handledPosition, strPatternLength);
            return sbuf.toString();
        } else {
            return strPattern;
        }
    }
}
