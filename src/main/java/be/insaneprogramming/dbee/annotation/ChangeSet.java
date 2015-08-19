package be.insaneprogramming.dbee.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set of changes to be added to the DB. Many changesets are included in one changelog.
 *
 * @see ChangeLog
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeSet {

    /**
     * Author of the changeset. Obligatory
     *
     * @return author
     */
    public String author();  // must be set

    /**
     * Sequence that provide correct order for changesets. Sorted alphabetically, ascending. Obligatory.
     *
     * @return ordering
     */
    public int order();   // must be set

    /**
     * Executes the change set on every mongobee's execution, even if it has been run before. Optional (default is false)
     *
     * @return should run always?
     */
    public boolean runAlways() default false;

    public boolean failOnError() default true;
}
