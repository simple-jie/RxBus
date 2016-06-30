package com.simplejie.toolkit.rxbus.annotation;

import com.simplejie.toolkit.rxbus.PostingThread;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Xingbo.Jie on 27/6/16.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    int[] eventId() default {};
    PostingThread thread() default PostingThread.CURRENT;
}
