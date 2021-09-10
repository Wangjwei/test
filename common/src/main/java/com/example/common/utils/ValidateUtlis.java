package com.example.common.utils;

import com.example.common.entity.BaseConstance;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api("验证工具类")
public class ValidateUtlis {

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("被注释的元素必须是手机号")
	public @interface ApiModelValidateMobile {
		String msgCode() default BaseConstance.SYS_ERROR_CODE_0005;
	}
	
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("被注释的元素必须是电子邮箱地址")
	public @interface ApiModelValidateEmail {
		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0005;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("被注释的元素必须在合适的范围内")
	public @interface ApiModelValidateRange {
		long min() default 0;

		long max() default Long.MAX_VALUE;

		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0008;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("被注释的字符串的大小必须在指定的范围内")
	public @interface ApiModelValidateLength {
		long min() default 0;

		long max() default Long.MAX_VALUE;

		String msgCode() default  BaseConstance.SYS_ERROR_CODE_0006;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("该注解元素的值只允许输入指定内容")
	public @interface ApiModelValidateListValue {
		String[] value();

		String msgCode() default BaseConstance.SYS_ERROR_CODE_0007;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@ApiOperation("被注释的元素必须符合指定的正则表达式")
	public @interface ApiModelValidatePattern {
		String regex();

		String msgCode() default BaseConstance.SYS_ERROR_CODE_0005;
	}

	private final static String REGEX_MOBILE = "^[1][3,4,5,7,8][0-9]{9}$";
	private final static String REGEX_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	public final static String REGEX_DATE = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
	public final static String REGEX_FLOAT = "^\\d+(\\.\\d+)?$";
	public static final String REGEX_INT = "^\\+?[1-9][0-9]*$";

	public static boolean validatorMobile(String value) {
		return validatorPattern(value, REGEX_MOBILE);
	}

	public static boolean validatorEmail(String value) {
		return validatorPattern(value, REGEX_EMAIL);
	}

	public static boolean validatorLength(String value, long min, long max) {
		long length = value.length();
		return length >= min && length <= max;
	}

	public static boolean validatorRange(long value, long min, long max) {
		return value >= min && value <= max;
	}

	public static boolean validatorListValue(String value, String[] values) {
		return validatorPattern(value,arrayToStr(values));
	}

	public static boolean validatorPattern(String value, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return  matcher.matches();
	}

	@ApiOperation("数组转字符串")
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
