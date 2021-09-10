package com.example.common.utils;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class XMLHandle {

	@ApiOperation("从InputStream中读取Document对象")
    public static Document openXmlDocument(String xml) { 
    	 InputStream is = null;
         Document doc  = null;   
         try {
			is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
			SAXReader reader = new SAXReader();
			doc = reader.read(is); 
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			if(is != null)
			{
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
         return doc; 
    } 
    
    
    public static void main(String[] args) {
    	System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//		String xml = "<?xml version=\"1.0\"?>				<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">				  <S:Body>				    <ns2:getDitSalesItemResponse xmlns:ns2=\"http://sinoajax.com/\">				      <return>				        <bar_code>70188965</bar_code>				        <bill_num>256359</bill_num>				        <finish_time>2018-12-25 15:50:00</finish_time>				        <gasstation_no>XR05</gasstation_no>				        <oilgun_no>0</oilgun_no>				        <pos_no>1</pos_no>				        <req_time>2018-12-25 16:27:36</req_time>				        <settle_day>2018-12-25</settle_day>				        <trans_amt>7.50</trans_amt>				        <trans_code>0</trans_code>				        <trans_prc>7.50</trans_prc>				        <trans_q>1.00</trans_q>				        <trans_type>1</trans_type>				      </return>				      <return>				        <bar_code>300874</bar_code>				        <bill_num>256360</bill_num>				        <discount_amt>0.00</discount_amt>				        <finish_time>2018-12-25 15:51:00</finish_time>				        <gasstation_no>XR05</gasstation_no>				        <oilgun_no>13</oilgun_no>				        <pay_amt>350.00</pay_amt>				        <pay_mode>1</pay_mode>				        <pos_no>1</pos_no>				        <req_time>2018-12-25 16:27:36</req_time>				        <settle_day>2018-12-25</settle_day>				        <trans_amt>350.00</trans_amt>				        <trans_code>0</trans_code>				        <trans_prc>7.17</trans_prc>				        <trans_q>48.81</trans_q>				        <trans_type>1</trans_type>				      </return>				    </ns2:getDitSalesItemResponse>				  </S:Body>				</S:Envelope>";
//		XMLHandle.getSoapBodyXmlString(xml);
    }
}
