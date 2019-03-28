package com.android.network.http.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * https网络核心层
 * TODO 待验证
 */
public class HttpsEngine {

    private final static String TAG = "HttpsEngine";
    private final static String ENCODE_TYPE = "utf-8";
    private final static int TIMEOUT_IN_MILLIONS = 15 * 1000;
    private String requestMethod = "GET";
    private String contentType = "application/x-www-form-urlencoded";
    private Context context;


    public String sendHttpRequest(String urlStr, String params) {
        HttpsURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        PrintWriter out = null;
        int responseCode = -1;
        String result = "网络异常";
        try {
            X509Certificate x509Certificate = getX509Certificate();

            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            TrustManager[] tm = new TrustManager[]{new MyTrustManager(x509Certificate)};
            SSLContext sslContext = SSLContext.getInstance("SSL");
//            SSLContext sslContext = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
            sslContext.init(null, tm, new SecureRandom());
            // 得到SSLSocketFactory对象
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            URL realUrl = new URL(urlStr);
            connection = (HttpsURLConnection) realUrl.openConnection();
            // 设置连接主机超时
            connection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            // 设置从主机读取数据超时
            connection.setReadTimeout(TIMEOUT_IN_MILLIONS);
            // 设置请求方式
            connection.setRequestMethod(requestMethod);

            connection.setHostnameVerifier(new MyHostnameVerifier());
            connection.setSSLSocketFactory(sslSocketFactory);

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Charset", ENCODE_TYPE);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Response-Type", "json");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setRequestProperty("Content-Type", "application/json");
            // 设置POST方式
            if ("POST".equals(requestMethod.toUpperCase())) {
                // Post请求不能使用缓存
                connection.setUseCaches(false);
                // 发送POST请求必须设置如下两行
                connection.setDoOutput(true);
                connection.setDoInput(true);
                // 设置参数
                if (!TextUtils.isEmpty(params) && !"".equals(params.trim())) {
                    // 获取URLConnection对象对应的输出流
                    out = new PrintWriter(connection.getOutputStream());
                    // 发送请求参数
                    out.print(params);
                    // flush 输出流的缓冲
                    out.flush();
                }
            }
            // 发送请求设置连接，也可以不写
            connection.connect();
            // 获得服务器响应的结果和状态码
            responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                is = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                result = baos.toString();
            } else {
                Log.d(TAG, "responseCode is not 200");
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception");
            e.printStackTrace();
        } finally { // 关闭资源
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    /**
     * 证书信任管理器（用于https请求）,X509 一种证书格式
     * 证书管理器的作用就是让它信任我们指定的证书。
     * Android 手机有一套共享证书的机制，如果目标URL服务器下发的证书不在已信任的证书列表里，或者该证书是自签名的，不是由权威机构颁发，那么会出异常。
     */
    static class MyTrustManager implements X509TrustManager {

        private X509Certificate mX509Certificate;

        MyTrustManager(X509Certificate x509Certificate) {
            this.mX509Certificate = x509Certificate;
        }

        //do nothing，接受任意客户端证书
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        //do nothing，接受任意服务端证书
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            for (X509Certificate certificate : chain) {
                certificate.checkValidity(); // 检查证书是否有效
                try {
                    certificate.verify(mX509Certificate.getPublicKey()); // 此处抛异常，原因待查询
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
            return null;   // 返回一个空对象
        }
    }

    /**
     * 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
     * 如果回调内实现不恰当，默认接受所有域名，则有安全风险。
     */
    static class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.d(TAG, "hostname=" + hostname);
            // Always return true，接受任意域名服务器'
            if ("www.12306.cn".equals(hostname) || "www.baidu.com".equals(hostname))
                return true;
            else
                return false;
        }
    }

    private X509Certificate getX509Certificate() throws CertificateException, IOException {
        InputStream inputStream = context.getAssets().open("srca.cer");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
        return certificate;
    }

}