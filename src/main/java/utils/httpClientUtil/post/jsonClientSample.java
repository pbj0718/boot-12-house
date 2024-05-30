package utils.httpClientUtil.post;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description: application格式请求
 * @author: pbj
 * @date: 2024-05-30
 * @version: V1.0
 */
@Component
@Slf4j
public class jsonClientSample {

    /**
     * post请求-发送application/json格式请求
     */
    public void doPostJsonClient() {
        String url = "http://localhost:8080/test";
        String contentType = "application/json; charset=UTF-8";
        String result;
        //设置请求头
        Map<String, Object> mapHead = new HashMap<>(16);
        mapHead.put("Content-Type", contentType);
        mapHead.put("Accept-Encoding", "");
        mapHead.put("Accept", "application/json");
        CloseableHttpClient httpClient = createSslClient(url);
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            //设置请求的请求体
            Map<Object, Object> map = new HashMap<>(16);

            //循环增加header
            for (Object o : mapHead.entrySet()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) o;
                post.addHeader(elem.getKey(), elem.getValue());
            }
            //添加请求体
            if (MapUtil.isNotEmpty(map)) {
                StringEntity requestEntity = new StringEntity(JSONUtil.toJsonStr(map), StandardCharsets.UTF_8);
                requestEntity.setContentType(ContentType.APPLICATION_JSON.toString());
                post.setEntity(requestEntity);
            }
            //发送请求并接收返回数据
            response = httpClient.execute(post);

            log.info("响应状态码"
                    + response.getStatusLine().getStatusCode() + "  "
                    + response.getStatusLine().getReasonPhrase() + "    "
                    + response.getStatusLine().getProtocolVersion());

            if (response.getStatusLine().getStatusCode() != 200) {
                log.info("响应异常response:{}", response);
            }
            //获取response的body部分
            HttpEntity entity = response.getEntity();
            //读取reponse的body部分并转化成字符串
            result = EntityUtils.toString(entity);
            log.info("响应结果" + result);
            log.info("请求成功");
        } catch (Exception e) {
            log.error("请求失败", e);
        } finally {
            try {
                httpClient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 忽略SSL证书校验的CloseableHttpClient.
     */
    public static CloseableHttpClient createSslClient(String url) {
        // 在JSSE中，证书信任管理器类就是实现了接口X509TrustManager的类。我们可以自己实现该接口，让它信任我们指定的证书。
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化,信任所有
        X509TrustManager x509mgr = new X509TrustManager() {
            //　　该方法检查客户端的证书，若不信任该证书则抛出异常
            @Override
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            // 　　该方法检查服务端的证书，若不信任该证书则抛出异常
            @Override
            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            // 　返回受信任的X509证书数组。
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext sslContext;
        try {
            // 此处设置的协议类型可以进行修改
            //sslContext = SSLContext.getInstance("TLS");
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    // 对hostname不做验证信任所有，此处有安全隐患
                    return true;
                }
            });
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(e.getMessage(), e);
        }
        return HttpClients.createDefault();
    }

    /**
     * json字符串转map
     * @param jsonStr
     * @return
     */
    public Map<String, Object> parseStr(String jsonStr) {
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        Map<String, Object> result = new HashMap<>();
        jsonToMap(jsonObject, result);

        return result;
    }

    /**
     * jsonObject转为map
     * @param o
     * @param result
     * @throws JSONException
     */
    public void jsonToMap(JSONObject o, Map result) throws JSONException {
        Iterator it1 = o.keys();
        Object jsValue = null;
        while (it1.hasNext()) {
            String jsonKey = (String) it1.next();
            jsValue = o.get(jsonKey);
            if (jsValue instanceof JSONObject) {
                Map sub = new HashMap();
                result.put(jsonKey, sub);
                jsonToMap((JSONObject) jsValue, sub);
            } else {
                result.put(jsonKey, jsValue);
            }
        }
    }

}
