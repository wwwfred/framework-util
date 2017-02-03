package net.wwwfred.framework.util.date;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DatetimeFormatAnnotation {
	DatetimeFormat value() default DatetimeFormat.STANDARED_DATE_TIME_FORMAT;
}
