package com.example.common.utils;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("baiduUtils")
@Api("微信小程序工具类")
public class BaiduUtils {

    @Value("${baidu.api.baseUrl}")
    private String baseUrl;

    @ApiModelProperty("身份证识别URL")
    @Value("${baidu.api.idCardOcrUrl}")
    private String idCardOcrUrl;

    @Value("${baidu.api.appId}")
    private String appId;

    @Value("${baidu.api.apiKey}")
    private String apiKey;

    @Value("${baidu.api.sercetKey}")
    private String sercetKey;

    private static final char last2byte = (char) Integer.parseInt("00000011", 2);
    private static final char last4byte = (char) Integer.parseInt("00001111", 2);
    private static final char last6byte = (char) Integer.parseInt("00111111", 2);
    private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
    private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
    private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
    private static final char[] encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public static BaiduUtils baiduUtils = null;

    private BaiduUtils() {
        super();
    }

    @PostConstruct
    public void init() {
        baiduUtils = this;
    }

    public static synchronized BaiduUtils newInstance() {
        if (baiduUtils == null) {
            baiduUtils = new BaiduUtils();
        }
        return baiduUtils;
    }

    /**
     * 微信api：accessToken接口调用
     * @return accessToken
     */
    public String getAccessToken() throws Exception {
        JSONObject accessToken = null;
        String accessTokenStr = JedisUtils.newInstance().get(this.getAppId() + "_accessToken_baiduapi");
        if (!StringUtils.isBlank(accessTokenStr)) {
            accessToken = JSONObject.parseObject(accessTokenStr);
        }
        if (accessToken == null) {
            String result = ClientUtil.newInstance(this.getBaseUrl() + "/oauth/2.0/token?grant_type=client_credentials&client_id=" + this.getApiKey() + "&client_secret=" + this.getSercetKey()).get();
            accessToken = JSONObject.parseObject(result);
            if (!StringUtils.isBlank(accessToken.getString("access_token"))) {
                JedisUtils.newInstance().set(this.getAppId() + "_accessToken_baiduapi", result, accessToken.getIntValue("expires_in") - 200);
            } else {
                log.error("获取accessToken失败：" + accessToken.getString("error") + accessToken.getString("error_description"));
                accessToken = null;
            }
        }
        if (accessToken != null) {
            return accessToken.getString("access_token");
        }

        return null;
    }

    public String base64Encode(byte[] from) {
        StringBuilder to = new StringBuilder((int) ((double) from.length * 1.34D) + 3);
        int num = 0;
        char currentByte = 0;

        int i;
        for (i = 0; i < from.length; ++i) {
            for (num %= 8; num < 8; num += 6) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                    case 1:
                    case 3:
                    case 5:
                    default:
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & lead2byte) >>> 6);
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & lead4byte) >>> 4);
                        }
                }

                to.append(encodeTable[currentByte]);
            }
        }

        if (to.length() % 4 != 0) {
            for (i = 4 - to.length() % 4; i > 0; --i) {
                to.append("=");
            }
        }

        return to.toString();
    }

    public String postGeneralUrl(String generalUrl, String contentType, String params, String encoding)
            throws Exception {
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(params.getBytes(encoding));
        out.flush();
        out.close();

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (String key : headers.keySet()) {
            System.err.println(key + "--->" + headers.get(key));
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), encoding));
        String result = "";
        String getLine;
        while ((getLine = in.readLine()) != null) {
            result += getLine;
        }
        in.close();
        System.err.println("result:" + result);
        return result;
    }

    /**
     * @return the idCardOcrUrl
     */
    public String getIdCardOcrUrl() {
        return idCardOcrUrl;
    }

    /**
     * @param idCardOcrUrl the idCardOcrUrl to set
     */
    public void setIdCardOcrUrl(String idCardOcrUrl) {
        this.idCardOcrUrl = idCardOcrUrl;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the sercetKey
     */
    public String getSercetKey() {
        return sercetKey;
    }

    /**
     * @param sercetKey the sercetKey to set
     */
    public void setSercetKey(String sercetKey) {
        this.sercetKey = sercetKey;
    }


    /**
     * @return the baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @param baseUrl the baseUrl to set
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    /*********** 调用具体api 接口 *****************/

    /**
     * 百度api：身份证识别接口调用
     *
     * @param imgData      身份证图片
     * @param id_card_side
     * @return
     * @throws Exception
     */
    public String idCardOcr(byte[] imgData, String id_card_side) throws Exception {
        try {
//            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = base64Encode(imgData);
            // 识别身份证正面id_card_side=front;识别身份证背面id_card_side=back;
            String params = "id_card_side=" + id_card_side + "&" + URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(imgStr, "UTF-8");
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            String result = this.postGeneralUrl(getIdCardOcrUrl() + "?access_token=" + this.getAccessToken(), "application/x-www-form-urlencoded", params, (getIdCardOcrUrl().contains("nlp") ? "GBK" : "UTF-8"));
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

}
