package mybatisPlusDataSourceSwitch.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 传递请求头信息,feign调用请求针对性优化
 **/
@Component
public class BusiFeignInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        // 从上下文获取请求对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            // 获取request请求对象
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            // 获取Attribute中的数据源编码信息
            String sourceKey = (String) request.getAttribute("dataSourceKey");
            if(StringUtils.isBlank(sourceKey)){
                sourceKey = request.getHeader("sourceKey");
            }
            // 因为通过feign之后在本项目中Attribute值和header值都没了，这里set通过feign可以获取到
            template.header("sourceKey", sourceKey);
        }
    }
}
