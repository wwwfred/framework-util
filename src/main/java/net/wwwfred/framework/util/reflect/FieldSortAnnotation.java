package net.wwwfred.framework.util.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记字段将被排序，值反映排序的序数
 * @author wangwwy
 * 2014年7月29日 下午8:41:56
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FieldSortAnnotation {
	int value() default 0;
}
