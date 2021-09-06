package com.example.test.utils;

import io.swagger.annotations.Api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api("验证工具类")
public class ValidateUtlis {

	/**
	 * 
	 * @时间:2015年11月18日下午1:10:00
	 * @描述:被注释的元素必须是手机号
	 * @作者:Yuan.pan
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidateMobile {
		String msgCode() default BaseConstance.SYS_ERROR_CODE_0005;
	}
	

	/**
	 * 
	 * @时间:2015年11月18日下午1:10:17
	 * @描述:被注释的元素必须是电子邮箱地址
	 * @作者:Yuan.pan
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidateEmail {
		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0005;
	}

	/**
	 * @时间:2015年11月18日下午1:10:37
	 * @描述:被注释的元素必须在合适的范围内
	 * @作者:Yuan.pan
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidateRange {
		long min() default 0;

		long max() default Long.MAX_VALUE;

		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0008;
	}

	/**
	 * @时间:2015年11月18日下午1:16:57
	 * @描述: 被注释的字符串的大小必须在指定的范围内
	 * @作者:Yuan.pan
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidateLength {
		long min() default 0;

		long max() default Long.MAX_VALUE;

		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0006;
	}

	/**
	 * 
	 * @时间:2015年11月18日下午1:18:12
	 * @描述:该注解元素的值只允许输入指定内容
	 * @作者:Yuan.pan
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidateListValue {
		String[] value();

		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0007;
	}

	/**
	 * 
	 * @时间:2015年11月18日下午1:18:12
	 * @描述:被注释的元素必须符合指定的正则表达式
	 * @作者:Yuan.pan
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ApiModelValidatePattern {
		String regex();

		String msgCode() default BaseConstance.SYS_ERROR_CODE_0005;
	}

	private final static String REGEX_MOBILE = "^[1][3,4,5,7,8][0-9]{9}$";
	private final static String REGEX_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	public final static String REGEX_DATE = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
	public final static String REGEX_FLOAT = "^\\d+(\\.\\d+)?$";
	public static final String REGEX_INT = "^\\+?[1-9][0-9]*$";

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean validatorMobile(String value) {
		return validatorPattern(value, REGEX_MOBILE);
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static boolean validatorEmail(String value) {
		return validatorPattern(value, REGEX_EMAIL);
	}

	/**
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean validatorLength(String value, long min, long max) {
		long length = value.length();
		return length >= min && length <= max;
	}

	/**
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean validatorRange(long value, long min, long max) {
		return value >= min && value <= max;
	}

	/**
	 * 
	 * @param value
	 * @param values
	 * @return
	 */
	public static boolean validatorListValue(String value, String[] values) {
		return validatorPattern(value,arrayToStr(values));
	}

	/**
	 * 
	 * @param regex
	 * @return
	 */
	public static boolean validatorPattern(String value, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return  matcher.matches();
	}

	/**
	 * 数组转字符串
	 * 
	 * @param values
	 * @return
	 */
	public static String arrayToStr(String[] values) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]);
			
			if(i< values.length-1){
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
}
