package com.example.admin.utils;


import org.springframework.stereotype.Component;

//import com.sun.mail.util.MailSSLSocketFactory;

@Component("mailQQUtils")
public class MailQQUtils {
/*
	 @Value("${mail.qq.account}")
	 private String account;// 登录账户
	 @Value("${mail.qq.password}")
	 private String password;// 登录密码
	 @Value("${mail.qq.host}")
	 private String host;// 服务器地址
	 @Value("${mail.qq.port}")
	 private String port;// 端口
	 @Value("${mail.qq.protocol}")
	 private String protocol;// 协议
	 @Value("${mail.qq.sender}")
	 private String sender;// 协议
	 
	 private static MailQQUtils mailQQUtils;
	 
	 private MailQQUtils (){
			super();
		}

	 @PostConstruct
     public void init() {
		 mailQQUtils = this;
     }
		
	 *//**
	  * 
	  * @return
	  *//*
	 public static synchronized MailQQUtils getInstance(){
		if(mailQQUtils == null){
			mailQQUtils = new MailQQUtils();
		}
		return mailQQUtils;
	 }
	 
	 //初始化参数
	 public Session initProperties() {
	     Properties properties = new Properties();
	     properties.setProperty("mail.transport.protocol", getProtocol());
	     properties.setProperty("mail.smtp.host", getHost());
	     properties.setProperty("mail.smtp.port", getPort());
	     // 使用smtp身份验证
	     properties.put("mail.smtp.auth", "true");
	     // 使用SSL,企业邮箱必需 start
	     // 开启安全协议
	     MailSSLSocketFactory mailSSLSocketFactory = null;
	     try {
	         mailSSLSocketFactory = new MailSSLSocketFactory();
	         mailSSLSocketFactory.setTrustAllHosts(true);
	     } catch (GeneralSecurityException e) {
	         e.printStackTrace();
	     }
	     properties.put("mail.smtp.enable", "true");
	     properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
	     properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	     properties.put("mail.smtp.socketFactory.fallback", "false");
	     properties.put("mail.smtp.socketFactory.port", getPort());
	     Session session = Session.getInstance(properties, new Authenticator() {
	         @Override
	         protected PasswordAuthentication getPasswordAuthentication() {
	             return new PasswordAuthentication(getAccount(), getPassword());
	         }
	     });
	     // 使用SSL,企业邮箱必需 end
	     // TODO 显示debug信息 正式环境注释掉
	     session.setDebug(true);
	     return session;
	 }
	 *//**
	  * 
	  * @param subject 邮件主题
	  * @param content 邮件内容
	  * @param receiverList 接收者列表,多个接收者之间用","隔开
	  * @param fileSrc 附件地址
	  *//*
	 public void send(String subject, String content, String receiverList, String fileSrc) {
	     try {
	         Session session = initProperties();
	         MimeMessage mimeMessage = new MimeMessage(session);
	         mimeMessage.setFrom(new InternetAddress(getAccount(), getSender()));// 发件人,可以设置发件人的别名
	         // 收件人,多人接收
	         InternetAddress[] internetAddressTo = new InternetAddress().parse(receiverList);
	         mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
	         
	         if(!StringUtils.isBlank(subject))
	         {
	        	// 主题
		         mimeMessage.setSubject(subject);
	         }
	         
	         // 时间
	         mimeMessage.setSentDate(new Date());
	         // 容器类 附件
	         MimeMultipart mimeMultipart = new MimeMultipart();
	         // 可以包装文本,图片,附件
	         MimeBodyPart bodyPart = null;
	         
	         if(!StringUtils.isBlank(content))
	         {
		         // 设置内容
	        	 bodyPart = new MimeBodyPart();
		         bodyPart.setContent(content, "text/html; charset=UTF-8");
		         mimeMultipart.addBodyPart(bodyPart);
	         }
	         
	         
	         if(!StringUtils.isBlank(fileSrc))
	         {
		         // 添加图片&附件
		         bodyPart = new MimeBodyPart();
		         bodyPart.attachFile(fileSrc);
		         mimeMultipart.addBodyPart(bodyPart);
	         }
	         mimeMessage.setContent(mimeMultipart);
	         mimeMessage.saveChanges();
	         Transport.send(mimeMessage);
	     } catch (MessagingException e) {
	         e.printStackTrace();
	     } catch (UnsupportedEncodingException e) {
	         e.printStackTrace();
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	 }

	*//**
	 * @return the account
	 *//*
	public String getAccount() {
		return account;
	}

	*//**
	 * @param account the account to set
	 *//*
	public void setAccount(String account) {
		this.account = account;
	}

	*//**
	 * @return the password
	 *//*
	public String getPassword() {
		return password == null?null:PasswordUtil.decryptPassword(password);
	}

	*//**
	 * @param password the password to set
	 *//*
	public void setPassword(String password) {
		this.password = password;
	}

	*//**
	 * @return the host
	 *//*
	public String getHost() {
		return host;
	}

	*//**
	 * @param host the host to set
	 *//*
	public void setHost(String host) {
		this.host = host;
	}

	*//**
	 * @return the port
	 *//*
	public String getPort() {
		return port;
	}

	*//**
	 * @param port the port to set
	 *//*
	public void setPort(String port) {
		this.port = port;
	}

	*//**
	 * @return the protocol
	 *//*
	public String getProtocol() {
		return protocol;
	}

	*//**
	 * @param protocol the protocol to set
	 *//*
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	*//**
	 * @return the sender
	 *//*
	public String getSender() {
		return sender;
	}

	*//**
	 * @param sender the sender to set
	 *//*
	public void setSender(String sender) {
		this.sender = sender;
	}
	 */
	 
}
