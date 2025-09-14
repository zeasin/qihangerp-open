package cn.qihangerp.api.common;

import java.math.BigDecimal;
import java.util.Objects;

public class NumberUtil {

    public static boolean equals(Number number1, Number number2) {
        return number1 instanceof BigDecimal && number2 instanceof BigDecimal ? equals((BigDecimal)number1, (BigDecimal)number2) : Objects.equals(number1, number2);
    }

}
