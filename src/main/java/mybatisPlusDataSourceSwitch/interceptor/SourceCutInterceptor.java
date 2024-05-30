package mybatisPlusDataSourceSwitch.interceptor;

//import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import mybatisPlusDataSourceSwitch.dataSourceEnum.DataSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 数据源切换-Mybatisplus多数据源切换
 **/
@Slf4j
@Component
public class SourceCutInterceptor implements HandlerInterceptor {

    @Value("${spring.datasource.webcutsource:master}")
    private String webcutsource;

    /**
     * 数据源分源标识
     */
    private static final String SOURCE_KEY = "dataSourceFlag";

    /**
     * 处理器运行之前执行
     **/
    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {

        //依据配置文件中的 web数据源切换标识 判断是否执行拦截: 默认拦截
        if(!"true".equals(webcutsource)){
            return true;
        }

        // 获取请求头中的 数据源分源标识
        String sourceKey = request.getHeader(SOURCE_KEY);
        // TODO 添加处理规则
        //请求头中不存在source key
        if(StringUtils.isBlank(sourceKey)){

        }
        String dataSourceKey = DataSourceEnum.getDataSource(sourceKey);

        log.info("{} 使用数据源: {}",request.getRequestURI(),dataSourceKey);
        if(StringUtils.isNotBlank(dataSourceKey)){
            //DynamicDataSourceContextHolder.push(dataSourceKey);
        }
        return true;
    }

    /**
     * 处理器运行之后执行
     **/
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) {

    }

    /**
     * 所拦截器的后置执行全部结束后，执行该操作
     **/
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        //清除当前线程源
        //DynamicDataSourceContextHolder.clear();
    }
}
