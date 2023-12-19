package com.networkprobe.core.annotation.miscs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Obsolete
@Documented(done = false)
@Retention(RetentionPolicy.SOURCE)
public @interface Replaced {

    Class<?> newer();

    String[] reference();

    String since() default "";

}
