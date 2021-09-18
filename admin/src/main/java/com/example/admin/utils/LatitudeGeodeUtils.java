package com.example.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Api("根据地址获取经纬度 高德")
public class LatitudeGeodeUtils {
	//公司申请的f2768f6805921fc04992c9d390940be6
	public static final String key = "f2768f6805921fc04992c9d390940be6";
	/**
	 * 输入地址  返回 经纬度坐标 key lng(经度),lat(纬度)
	 */
	public static Map<String, String> getGeocoderLatitude(String address) {
		BufferedReader in = null;
		try {
			// 将地址转换成utf-8的16进制
			address = URLEncoder.encode(address, "UTF-8");				
			URL tirc = new URL("http://restapi.amap.com/v3/geocode/geo?key="+key+"&s=rsv3&address="+address);		
			in = new BufferedReader(new InputStreamReader(tirc.openStream(), StandardCharsets.UTF_8));
			String res;
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String str = sb.toString();			
			JSONObject jsonObject = JSON.parseObject(str);
			JSONArray jsonArray = (JSONArray) jsonObject.get("geocodes");
			Map<String, String> map = null;
			if (StringUtils.isNotEmpty(str)&&jsonObject.get("info").equals("OK")) {
				String location=JSON.parseObject(jsonArray.get(0).toString()).get("location").toString();
					String lng = location.split(",")[0].toString();
					String lat = location.split(",")[1].toString();
					map = new HashMap<String, String>();
					map.put("lng", lng);
					map.put("lat", lat);
					return map;
				}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				assert in != null;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 输入经纬度坐标 key lng(经度),lat(纬度) location="116.397428,39.90923"  返回 详细地址
	 */
	public static String getAddByLat(String location) {
		BufferedReader in = null;
		try {			
			URL tirc = new URL("http://restapi.amap.com/v3/geocode/regeo?key="+key+"&s=rsv3&location="+location);	
			in = new BufferedReader(new InputStreamReader(tirc.openStream(), StandardCharsets.UTF_8));
			String res;
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String str = sb.toString();		
			if (StringUtils.isNotEmpty(str)) {
				JSONObject jsonObj = JSONObject.parseObject(str);
                 if(jsonObj.get("info").equals("OK")){
                	 JSONObject resultObj = jsonObj.getJSONObject("regeocode"); 
                	 return resultObj.getString("formatted_address");
                 }				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				assert in != null;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * lat(纬度)lng(经度) 
	 */
	public static Double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
		String lat11=String.valueOf(lat1);
		String lng11=String.valueOf(lng1);
		String lat22=String.valueOf(lat2);
		String lng22=String.valueOf(lng2);	
		BufferedReader in = null;
		try {	
			String url="http://restapi.amap.com/v3/direction/driving?origin="+lng11+","+lat11+"&destination="+lng22+","+lat22+"&strategy=0&extensions=&s=rsv3&key="+key;
			URL tirc = new URL(url);	
			in = new BufferedReader(new InputStreamReader(tirc.openStream(), StandardCharsets.UTF_8));
			String res;
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String str = sb.toString();		
			System.out.println(str);
			
			if (StringUtils.isNotEmpty(str)) {
				JSONObject jsonObj = JSONObject.parseObject(str);
                 if(jsonObj.get("info").equals("OK")){
                	 JSONArray jsonArray = (JSONArray)JSONObject.parseObject(jsonObj.get("route").toString()).get("paths");
               	 	String distance=JSON.parseObject(jsonArray.get(0).toString()).get("distance").toString();
               	 	return  Double.parseDouble(distance)/1000;
                 }				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				assert in != null;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	
	
	}

	public static void main(String[] args) {
		//latitude":"23.118484","longitude":"113.271629"
		System.out.println("广东省佛山市顺德区北滘镇西滘工业区二路");
		Map<String, String> map1=getGeocoderLatitude("广东省佛山市顺德区北滘镇西滘工业区二路");
		System.out.println(map1);
		System.out.println("广东省广州市越秀区珠光路北京大厦");
		Map<String, String> map2=getGeocoderLatitude("广东省广州市越秀区珠光路北京大厦");
		System.out.println(map2);
		
		System.out.println("两者距离：");
		assert map1 != null;
		assert map2 != null;
		System.out.println( GetDistance(Double.parseDouble(map1.get("lat")),Double.parseDouble(map1.get("lng")),Double.parseDouble(map2.get("lat")),Double.parseDouble(map2.get("lng"))));
	}

}
