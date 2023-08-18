package com.networkprobe.core.annotation;

public @interface Obsolete {

    String reason() default "obsolete";

}
