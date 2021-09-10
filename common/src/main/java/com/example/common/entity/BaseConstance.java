package com.example.common.entity;

public class BaseConstance {
    /**
     * 存放Authorization的header字段
     */
    public static final String AUTHORIZATION = "authorization";

    /**
     * 存储当前登录用户id的字段名
     */
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    /**
     * token有效期（分钟）
     */
    public static final int API_TOKEN_EXPIRES_MINUTE = 30;

    /**
     * 业务系统分类全部
     * 业务分类
     * 响应编码
     */
    public static final String source_sys_all = "000";
    public static final String service_class_all = "000";
    public static final String msg_result_code_all="000";
    /**
     *   是否启用1/停用0  2 全部
     */
    public static final String inuse_yes = "1";
    public static final String inuse_no = "0";
    public static final String inuse_all = "2";
    /**
     *   测试状态 0未测试 1 已测试  <服务表>
     */
    public static final String systemout_status_yes = "1";
    public static final String systemout_status_no = "0";
    /**
     *  测试是否成功 0 否 1是 <测试标记表>
     */
    public static final String test_status_yes = "1";
    public static final String test_status_no = "0";
    /**
     *  是否缓存   1缓存 0否
     */
    public static final String use_cache_yes = "1";
    public static final String use_cache_no = "0";
    /**
     *  是否重新加载 1重新加载 0不重新加载
     */
    public static final String is_real_init_yes = "1";
    public static final String is_real_init_no = "0";
    /**
     * 服务是否需要验证token   0 否 1 是
     */
    public static final String is_check_token_yes = "1";
    public static final String is_check_token_no = "0";
    /**
     * 服务访问方式  0后台 1外部
     */
    public static final String access_type_out = "1";
    public static final String access_type_inside = "0";


    /**----------------------基本参数---------------------------*/

    /**
     * 成功
     */
    public final static String SYS_SUCCESS_CODE_0000 = "0";
    /**
     * 错误
     */
    /**
     * 系统登录超时，请重新登录。
     */
    public final static String SYS_ERROR_CODE_0001 = "SYS-0001";
    /**
     * 该页面，没有操作权限。
     */
    public final static String SYS_ERROR_CODE_0002 = "SYS-0002";
    /**
     * 未知错误，请联系系统管理员。
     */
    public final static String SYS_ERROR_CODE_0003 = "SYS-0003";
    /**
     * {0}不能为空
     */
    public final static String SYS_ERROR_CODE_0004 = "SYS-0004";
    /**
     * {0}格式不对
     */
    public final static String SYS_ERROR_CODE_0005 = "SYS-0005";
    /**
     * 长度必须介于{1}和{2}之间
     */
    public final static String SYS_ERROR_CODE_0006 = "SYS-0006";
    /**
     * {0}只允许输入{1}
     */
    public final static String SYS_ERROR_CODE_0007 = "SYS-0007";
    /**
     * {0}必须在{1}和{2}范围内
     */
    public final static String SYS_ERROR_CODE_0008 = "SYS-0008";
    /**
     * 数据重复
     */
    public final static String SYS_ERROR_CODE_0009 = "SYS-0009";

    /**
     *服务配置不存在
     */
    public static final String SYS_ERROR_CODE_5001 = "SYS_5001";
    /**
     * 获取服务配置异常
     */
    public static final String SYS_ERROR_CODE_5002 = "SYS_5002";
    /**
     * 获取脚本文件出错
     */
    public static final String SYS_ERROR_CODE_5003 = "SYS_5003";
    /**
     * 执行脚本异常
     */
    public static final String SYS_ERROR_CODE_5004 = "SYS_5004";
    /**
     * 调用服务异常
     */
    public static final String SYS_ERROR_CODE_5005 = "SYS_5005";
    /**
     * jdbc连接异常
     */
    public static final String SYS_ERROR_CODE_5006 = "SYS_5006";
    /**
     * 实例化脚本异常
     */
    public static final String SYS_ERROR_CODE_5007 = "SYS_5007";
    /**
     * groovy返回xml   即组装格式为   {"xml_data":"xml字符串"}
     */
    public static final String SYS_ERROR_CODE_5008 = "SYS_5008";
    /**
     * data为json字符串格式
     */
    public static final String SYS_ERROR_CODE_5009 = "SYS_5009";
    /**
     *数据库不存在此服务
     */
    public static final String SYS_ERROR_CODE_5010 = "SYS_5010";
    /**
     *head头部用户名与url中用户名不一致
     */
    public static final String SYS_ERROR_CODE_5011 = "SYS_5011";
    /**
     *head头部系统分类与url中用系统分类不一致
     */
    public static final String SYS_ERROR_CODE_5012 = "SYS_5012";
    /**
     *head头部业务分类与url中业务分类不一致
     */
    public static final String SYS_ERROR_CODE_5013 = "SYS_5013";
    /**
     *head头部业务编码与url中业务编码不一致
     */
    public static final String SYS_ERROR_CODE_5014 = "SYS_5014";
    /**
     *用户名错误
     */
    public static final String SYS_ERROR_CODE_5015 = "SYS_5015";
    /**
     *系统分类错误
     */
    public static final String SYS_ERROR_CODE_5016 = "SYS_5016";
    /**
     *业务分类错误
     */
    public static final String SYS_ERROR_CODE_5017 = "SYS_5017";
    /**
     *业务编码错误
     */
    public static final String SYS_ERROR_CODE_5018 = "SYS_5018";
    /**
     *用户口令错误
     */
    public static final String SYS_ERROR_CODE_5019 = "SYS_5019";
    /**
     *JSON中head属性不存在
     */
    public static final String SYS_ERROR_CODE_5020 = "SYS_5020";
    /**
     *获取客户端信息失败
     */
    public static final String SYS_ERROR_CODE_5021 = "SYS_5021";





    /**
     *
     */
    public static final String SYS_ERROR_CODE_6001 = "SYS_6001";
    /**
     * 请求报文不能为空
     */
    public static final String SYS_ERROR_CODE_6002 = "SYS_6002";
    /**
     * 请求参数JSON格式不正确
     */
    public static final String SYS_ERROR_CODE_6003 = "SYS_6003";
    /**
     * 验证请求参数JSON格式异常
     */
    public static final String SYS_ERROR_CODE_6004 = "SYS_6004";
    /**
     * JDBC为空
     */
    public static final String SYS_ERROR_CODE_6005 = "SYS_6005";
    /**
     * JDBC连接异常
     */
    public static final String SYS_ERROR_CODE_6006 = "SYS_6006";
    /**
     * 实例化groovy脚本出错
     */
    public static final String SYS_ERROR_CODE_6007 = "SYS_6007";
    /**
     * 调用groovy脚本出错
     */
    public static final String SYS_ERROR_CODE_6008 = "SYS_6008";

    /**
     * 草稿：0
     */
    public static final String AUDITING_STATUS_DRAFT = "0";
    /**
     * 审核中：1
     */
    public static final String AUDITING_STATUS_DOING = "1";
    /**
     * 审核通过：2
     */
    public static final String AUDITING_STATUS_PASS = "2";
    /**
     * 驳回：3
     */
    public static final String AUDITING_STATUS_REJECT = "3";
    /**
     * 审核未通过：4
     */
    public static final String AUDITING_STATUS_NOPASS = "4";
}
