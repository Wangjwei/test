package com.example.admin.utils;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientUtil {
	private CloseableHttpClient httpClient = null;
	private HttpPost method = null;
	private String url = null;
	
	public static ClientUtil newInstance(String url,String key,String certPath) throws Exception {
		ClientUtil client = new ClientUtil();
		CloseableHttpClient httpclient = null;
		httpclient = client.initCertHttpclient(key, certPath);
		client.setHttpClient(httpclient);
		HttpPost method = new HttpPost(url);
		client.setMethod(method);
		client.setUrl(url);
		return client;
	}
	
	public static ClientUtil newInstance(String url) {
		ClientUtil client = new ClientUtil();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		client.setHttpClient(httpclient);
		HttpPost method = new HttpPost(url);
		client.setMethod(method);
		client.setUrl(url);
		return client;
	}
	
	/**
     * 加载证书
     *
     */
    private CloseableHttpClient initCertHttpclient(String key,String certPath) throws Exception {
        
        // 证书的路径
        //String path = Configure.getCertPath();
        // 指定读取证书格式为PKCS12
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        // 读取本机存放的PKCS12证书文件
        FileInputStream instream = new FileInputStream(new File(certPath));
        try {
            // 指定PKCS12的密码(商户ID)
            keyStore.load(instream, key.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts
                .custom()
                .loadKeyMaterial(keyStore, key.toCharArray())
                .build();
        // 指定TLS版本（过时）
        /*SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);*/
		//新版写法二选一
		/*SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
				new String[] {"TLSv1"}, null,new DefaultHostnameVerifier());*/
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
				new String[] {"TLSv1"}, null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        // 设置httpclient的SSLSocketFactory
        return HttpClients
                .custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }

	

	/**
	 * POST JSON请求
	 * @param para
	 * @return  JSON字符串
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String postJson(String para,String sid) throws ClientProtocolException, IOException {
		String content = "";
        CloseableHttpResponse response = null;
        
		try {
			getMethod().addHeader("Content-type","application/json; charset=utf-8");
			getMethod().setHeader("Accept", "application/json");
			getMethod().setHeader("authorization", sid);
			getMethod().setEntity(new StringEntity(para, StandardCharsets.UTF_8));
			
			response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK) {
				throw new IOException("" + statusCode);
			}
			content = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			throw e;
		}finally {
        	close(response,httpClient);
        }
		return content;
	}
	
	/**
	 * PUT JSON请求
	 * @param para 
	 * @return  JSON字符串
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String putJson(String para,String sid) throws ClientProtocolException, IOException {
		String content = "";
        CloseableHttpResponse response = null;
		try {
			HttpPut httpPut = new HttpPut(getUrl());
			httpPut.addHeader("Content-type","application/json; charset=utf-8");
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("authorization", sid);
			httpPut.setEntity(new StringEntity(para, StandardCharsets.UTF_8));
			
			response = httpClient.execute(httpPut);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK) {
				throw new IOException("" + statusCode);
			}
			content = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			throw e;
		}finally {
        	close(response,httpClient);
        }
		return content;
	}
	
	/**
	 * POST
	 * @param paras
	 * @return
	 * @throws Exception
	 */
	public String post(Map<String, Object> paras) throws Exception {
		String content = "";
        CloseableHttpResponse response = null;
        
		try {
			if(paras != null)
			{
				List<NameValuePair> nvps = new ArrayList <NameValuePair>();
				Set<String> keys = paras.keySet();
				for(String k : keys) {
					nvps.add(new BasicNameValuePair(k, (String)paras.get(k)));
				}
				getMethod().setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
			}
			
			response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK) {
				throw new IOException("" + statusCode);
			}
			content = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			throw e;
		}finally {
        	close(response,httpClient);
        }
		return content;
	}
	
	/**
	 * 模仿soapui工具方式调用webservice服务
	 * @param SOAPAction 服务方法名
	 * @param soapStr soapenv:Envelope格式报文，可参考soapui工具生成并适当调整
	 * @param connectTimeout 连接的最长时间
	 * @param connectionRequestTimeout 从连接池中获取到连接的最长时间
	 * @param socketTimeout 数据传输的最长时间
	 * @return soapenv:Envelope格式报文，可参考soapui工具返回
	 * @throws Exception
	 */
	public String postSoap(String SOAPAction,String soapStr,int connectTimeout,int connectionRequestTimeout,int socketTimeout) throws Exception
	{
		String content = "";
        CloseableHttpResponse response = null;
        try {

            // 构建请求配置信息
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout) // 创建连接的最长时间
                    .setConnectionRequestTimeout(connectionRequestTimeout) // 从连接池中获取到连接的最长时间
                    .setSocketTimeout(socketTimeout) // 数据传输的最长时间
                    .build();
            getMethod().setConfig(config);
            
            //采用SOAP1.1调用服务端，这种方式能调用服务端为soap1.1和soap1.2的服务
        	getMethod().setHeader("Content-Type", "text/xml;charset=UTF-8");
        	getMethod().setHeader("SOAPAction", SOAPAction);

            //采用SOAP1.2调用服务端，这种方式只能调用服务端为soap1.2的服务
            // httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            StringEntity stringEntity = new StringEntity(soapStr, StandardCharsets.UTF_8);
            getMethod().setEntity(stringEntity);
            response = httpClient.execute(getMethod());
            
            // 判断返回状态是否为HttpStatus.SC_OK
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
            	//如果不是HttpStatus.SC_OK 会返回SOAPFault
            	content = EntityUtils.toString(response.getEntity(), "UTF-8");
            	throw new Exception(content);
            }
        } catch (Exception e) {
        	throw new Exception("调用下游接口失败",e);
        } finally {
        	close(response,httpClient);
        }

        return content;
	}
	
	/**
	 * 模仿soapui工具方式调用webservice服务
	 * @param SOAPAction 服务方法名
	 * @param soapStr soapenv:Envelope格式报文，可参考soapui工具生成并适当调整
	 * @return soapenv:Envelope格式报文，可参考soapui工具返回
	 * @throws Exception
	 */
	public String postSoap(String SOAPAction,String soapStr) throws Exception
	{
		String content = "";
        CloseableHttpResponse response = null;
        try {
            //采用SOAP1.1调用服务端，这种方式能调用服务端为soap1.1和soap1.2的服务
        	getMethod().setHeader("Content-Type", "text/xml;charset=UTF-8");
        	getMethod().setHeader("SOAPAction", SOAPAction);

            //采用SOAP1.2调用服务端，这种方式只能调用服务端为soap1.2的服务
            // httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
            StringEntity stringEntity = new StringEntity(soapStr, StandardCharsets.UTF_8);
            getMethod().setEntity(stringEntity);
            response = httpClient.execute(getMethod());
            
            // 判断返回状态是否为HttpStatus.SC_OK
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } else {
            	//如果不是HttpStatus.SC_OK 会返回SOAPFault
            	content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            	throw new Exception(content);
            }
        } catch (Exception e) {
        	throw new Exception("调用下游接口失败",e);
        } finally {
        	close(response,httpClient);
        }

        return content;
	}
	
	/**
	 * POST XML请求
	 * @param xmlparam
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String postXml(String xmlparam) throws ClientProtocolException, IOException {
		String content = "";
        CloseableHttpResponse response = null;
        
		try {
			getMethod().addHeader("Content-type","text/xml; charset=utf-8");
			getMethod().setHeader("Accept", "text/xml");
			getMethod().setEntity(new StringEntity(xmlparam, StandardCharsets.UTF_8));
			
			response = httpClient.execute(method);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != HttpStatus.SC_OK) {
				throw new IOException("访问 "+this.getUrl()+" 失败，状态码：" + statusCode);
			}
			content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
		} catch (ParseException e) {
			throw e;
		}finally {
        	close(response,httpClient);
        }
		return content;
	}
	
	/**
	 * get方式提交
	 * @return
	 * @throws Exception
	 */
	public String get() throws Exception
	{
		String content = "";
        //创建http get请求
        HttpGet httpGet = new HttpGet(getUrl());
        CloseableHttpResponse response = null;
        try {
            
            response = httpClient.execute(httpGet);
                        
            // 判断返回状态是否为HttpStatus.SC_OK
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
            	throw new Exception("调用下游接口失败，错误码："+response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
        	throw new Exception("调用下游接口失败",e);
        } finally {
        	close(response,httpClient);
        }

        return content;
	}
	
	public void close(Object...objects)
	{
		if(objects != null && objects.length > 0){
			try {
				for(Object o:objects){
					if(o instanceof CloseableHttpClient){
						((CloseableHttpClient)o).close();
					}else if(o instanceof CloseableHttpResponse){
						((CloseableHttpResponse)o).close();
					}
					o = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public HttpPost getMethod() {
		return method;
	}

	public void setMethod(HttpPost method) {
		this.method = method;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static void main(String[] args) {
		String url = "http://10.193.1.181:8080/DistService/GetDataService";
		StringBuilder soapBuilder = new StringBuilder(64);
        soapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        soapBuilder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://sinoajax.com/\">");
        soapBuilder.append("   <soapenv:Header/>");
        soapBuilder.append("       <soapenv:Body>");
        soapBuilder.append("             <web:getDitDayData>");
        soapBuilder.append("                     <arg0>").append("</arg0>");
        soapBuilder.append("                     <arg1>").append("2018-11-11").append("</arg1>");
        soapBuilder.append("               </web:getDitDayData>");
        soapBuilder.append("    </soapenv:Body>");
        soapBuilder.append("</soapenv:Envelope>");
        
        try {
			System.out.println(ClientUtil.newInstance(url).postSoap("getDitDayData", soapBuilder.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
