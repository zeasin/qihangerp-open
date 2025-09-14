package cn.qihangerp.api.common;

import java.util.Objects;

public class ObjectUtil {
//    /**
//     * 判断两个对象是否相等
//     *
//     * @param obj1 第一个对象
//     * @param obj2 第二个对象
//     * @return 如果相等返回 true，否则返回 false
//     */
//    public static boolean equal(Object obj1, Object obj2) {
//        if (obj1 == obj2) {
//            return true;  // 如果两个对象引用相同或都为 null，则相等
//        }
//
//        if (obj1 == null || obj2 == null) {
//            return false;  // 如果只有一个对象为 null，则不相等
//        }
//        if (obj1 instanceof String && obj2 instanceof String) {
//            return obj1.equals(obj2);
//        }
//        return obj1.equals(obj2);  // 调用对象的 equals 方法进行比较
//    }

    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }
    public static boolean equal(Object obj1, Object obj2) {
        return obj1 instanceof Number && obj2 instanceof Number ? NumberUtil.equals((Number)obj1, (Number)obj2) : Objects.equals(obj1, obj2);
    }

}
