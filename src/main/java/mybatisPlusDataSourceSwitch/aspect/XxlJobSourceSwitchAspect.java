package mybatisPlusDataSourceSwitch.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 数据源切换-MybatisPlus多数据源用
 **/
@Slf4j
@Aspect
@Component
public class XxlJobSourceSwitchAspect {

    @Pointcut(value = "@annotation(mybatisPlusDataSourceSwitch.aspect.XxlJobSourceSwitch)")
    public void pointcut() {}

    @Before("pointcut() && @annotation(cc)")
    public void doBefore(JoinPoint joinPoint, XxlJobSourceSwitch cc){

    }

}
