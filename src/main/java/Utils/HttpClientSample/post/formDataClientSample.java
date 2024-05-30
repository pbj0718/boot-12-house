package Utils.HttpClientSample.post;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.io.File;

/**
 * @description: form-data调用（okhttp请求）
 * @author: pbj
 * @date: 2024-05-30
 * @version: V1.0
 */
@Slf4j
public class formDataClientSample {

    /**
     * 采用okhttp发送form-data样式的post请求
     * 此方法示例支持传入file文件
     */
    public void sendFormDataClient() throws Exception {
        // 客户端调用忽略SSL认证
        final TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
        };
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        // Create an OkHttpClient with the SSLContext
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier((hostname, session) -> true);

        OkHttpClient client = builder.build();
        //OkHttpClient client = new OkHttpClient();

        // 构建form-data请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("field1", "value1")
                .addFormDataPart("field2", "value2")
                // 传入文件流
                .addFormDataPart("file", "filename.txt",
                        RequestBody.create(MediaType.parse("application/octet-stream"), new File("D:\\工具地址.txt")))
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url("http://127.0.0.1:3000/aaa/fileUpload.jsp")
                .post(requestBody)
                .addHeader("Content-Type", "application/form-data; charset=utf-8")
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            // 处理响应
            log.info(responseBody);
        }
    }

}
