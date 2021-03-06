package com.hwangfantasy.smartutil.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * @作者 yunfeiyang
 * @创建时间: 2017/5/4 <br/>
 * @方法描述: HttpUtil. <br/>
 */

public class HttpUtil {
    private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
    private static HttpClient client = new HttpClient(connectionManager);
    static {
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30 * 1000);
        client.getHttpConnectionManager().getParams().setMaxConnectionsPerHost(client.getHostConfiguration(),2000);
        client.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(2000);
        client.getHttpConnectionManager().getParams().setMaxTotalConnections(2000);
        client.getHttpConnectionManager().getParams().setSoTimeout(30 * 1000);
        client.setTimeout(30 * 1000);
        client.getParams().setConnectionManagerTimeout(5*1000);
    }

    public static String httpPostBody(String url, String body, String writerCharset, String readerCharset) throws Exception {
        String responseBody = null;
        if (writerCharset == null) {
            writerCharset = "UTF-8";
        }
        if (readerCharset == null) {
            readerCharset = "UTF-8";
        }

        PostMethod method = new PostMethod(url);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, writerCharset);
        method.setRequestBody(body);

        int status = client.executeMethod(method);
        if (status != HttpStatus.SC_OK) {
            throw new IllegalStateException("Method failed: " + method.getStatusLine());
        }
        responseBody = IOUtils.toString(method.getResponseBodyAsStream(), readerCharset);
        method.releaseConnection();
        return responseBody;
    }

    public static String httpPost(String url, Map<String, String> paras, String writerCharset, String readerCharset) throws Exception {
        String responseBody = null;
        if (writerCharset == null) {
            writerCharset = "UTF-8";
        }
        if (readerCharset == null) {
            readerCharset = "UTF-8";
        }
        PostMethod method = new PostMethod(url);
        if (paras != null) {
            for (Map.Entry<String, String> entry : paras.entrySet()) {
                method.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, writerCharset);

        int status = client.executeMethod(method);
        if (status != HttpStatus.SC_OK) {

            throw new IllegalStateException("Method failed: " + method.getStatusLine());
        }

        responseBody = IOUtils.toString(method.getResponseBodyAsStream(), readerCharset);
        method.releaseConnection();
        return responseBody;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @return URL所代表远程资源的响应
     * @throws Exception
     */

    public static String sendGet(String url) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 建立实际的连接
            conn.connect();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            throw new Exception("发送GET请求出现异常！", e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws Exception
     */
    public static String sendPost(String url, String param) throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        StringBuffer result = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            // int len = -1;
            // char[] c = new char[512];
            // while ((len = in.read(c)) != -1) {
            // result.append(c, 0, len);
            // }
        } catch (IOException e) {
            throw new Exception("发送 POST 请求出现异常！", e);
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
