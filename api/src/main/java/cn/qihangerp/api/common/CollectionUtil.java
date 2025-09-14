package cn.qihangerp.api.common;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionUtil {
    public static boolean isNotEmpty(Collection<?> list) {
        return list != null && !list.isEmpty();
    }

    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
