//
// Decompiled by Procyon v0.5.36
//

package com.application.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class H {
    public static <T> void each(final List<T> list, final Each<T> each) throws RuntimeException {
        if (!isTrue((Object) list)) {
            return;
        }
        for (int index = 0; index < list.size(); ++index) {
            each.do_(index, (T) list.get(index));
        }
    }


    public static <T> T find(final List<T> list, final Find<T> findAll_) {
        if (!isTrue((Object) list)) {
            return null;
        }
        for (int index = 0; index < list.size(); ++index) {
            if (findAll_.do_(index, (T) list.get(index))) {
                return list.get(index);
            }
        }
        return null;
    }

    public static <T> Boolean exists(final List<T> list, final Find<T> findAll_) {
        if (!isTrue((Object) list)) {
            return false;
        }
        for (int index = 0; index < list.size(); ++index) {
            if (findAll_.do_(index, (T) list.get(index))) {
                return true;
            }
        }
        return false;
    }

    public static <T1, T2> List<T2> collect(final List<T1> list, final Collect<T1, T2> collect) {
        final List<T2> newList = new ArrayList<T2>();
        if (!isTrue((Object) list)) {
            return newList;
        }
        for (int index = 0; index < list.size(); ++index) {
            newList.add((T2) collect.do_(index, (T1) list.get(index)));
        }
        return newList;
    }

    public static String join(final Collection<String> strs, final String delimiter) {
        if (strs == null || strs.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        final Iterator<String> iterator = strs.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(final String[] strs, final String delimiter) {
        if (strs == null || strs.length == 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < strs.length; ++index) {
            sb.append(strs[index]);
            if (index < strs.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static Boolean isTrue(final Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return !((String) value).trim().isEmpty();
        }
        if (value instanceof Number) {
            return !((Number) value).equals(0L);
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Collection) {
            return !((Collection) value).isEmpty();
        }
        if (value instanceof Object[]) {
            return ((Object[]) value).length > 0;
        }
        return true;
    }


    public static <T> T getOne(final List<T> list) {
        return ((boolean) isTrue((Object) list)) ? list.get(0) : null;
    }

    public interface Find<T> {
        Boolean do_(final int index, final T item) throws RuntimeException;
    }

    public interface Collect<T1, T2> {
        T2 do_(final int index, final T1 item) throws RuntimeException;
    }

    public interface Each<T> {
        void do_(final int index, final T item) throws RuntimeException;
    }
}
