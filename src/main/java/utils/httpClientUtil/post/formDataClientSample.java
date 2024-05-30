package utils.httpClientUtil.post;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    /**
     *
     * @param uriAPI
     * @param jsonHeads
     * @param localFile
     * @return
     */
    public void doPostForFromData(String uriAPI, JSONObject jsonHeads, File localFile) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("YOUR_ENDPOINT_URL");

        // 准备 form-data 参数
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("param1", "value1"));
        params.add(new BasicNameValuePair("param2", "value2"));

        // 设置编码格式为 UTF-8
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setCharset(StandardCharsets.UTF_8);

        // 添加文本参数
        for (BasicNameValuePair param : params) {
            builder.addTextBody(param.getName(), param.getValue(), ContentType.TEXT_PLAIN);
        }

        // 添加文件参数
        File file = new File("path_to_your_file");
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        // 构建请求实体
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        // 发送请求
        HttpResponse response = httpClient.execute(httpPost);

        // 处理响应
        int statusCode = response.getStatusLine().getStatusCode();
        log.info("Status Code: " + statusCode);
    }

}
