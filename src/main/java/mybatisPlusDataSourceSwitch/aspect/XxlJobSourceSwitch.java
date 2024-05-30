package mybatisPlusDataSourceSwitch.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源切换-MybatisPlus多数据源用
 * 定时任务部分，集成的xxljob模块
 **/
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlJobSourceSwitch {

    String name() default "";

}
