package net.wwwfred.framework.util.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识对象中简单属性表示为XML的ELEMENT
 * @author wangwwy
 * 2014年7月29日 下午8:41:56
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface XmlElementAnnotation {
}
