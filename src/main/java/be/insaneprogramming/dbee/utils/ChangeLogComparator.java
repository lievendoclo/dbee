package be.insaneprogramming.dbee.utils;

import java.io.Serializable;
import java.util.Comparator;

import be.insaneprogramming.dbee.annotation.ChangeLog;

import static org.springframework.util.StringUtils.hasText;

/**
 * Sort ChangeLogs by 'order' value or class name (if no 'order' is set)
 *
 * @author lstolowski
 * @since 2014-09-17
 */
public class ChangeLogComparator implements Comparator<Class<?>>, Serializable {

    @Override
    public int compare(Class<?> o1, Class<?> o2) {
        ChangeLog c1 = o1.getAnnotation(ChangeLog.class);
        ChangeLog c2 = o2.getAnnotation(ChangeLog.class);

        String val1 = !(hasText(c1.order())) ? o1.getCanonicalName() : c1.order();
        String val2 = !(hasText(c2.order())) ? o2.getCanonicalName() : c2.order();

        return val1.compareTo(val2);
    }
}
