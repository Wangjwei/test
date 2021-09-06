package com.example.test.utils;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api("sftp工具类")
@Slf4j
public class SFTPUtil {

    private ChannelSftp sftp;  
        
    private Session session;  
    /** SFTP 登录用户名*/    
    private String username; 
    /** SFTP 登录密码*/    
    private String password;  
    /** 私钥 */    
    private String privateKey;  
    /** SFTP 服务器地址IP地址*/    
    private String host;  
    /** SFTP 端口*/  
    private int port;  
        
    
    /**  
     * 构造基于密码认证的sftp对象  
     */    
    public SFTPUtil(String username, String password, String host, int port) {  
        this.username = username;  
        this.password = password;  
        this.host = host;  
        this.port = port;  
    } 
    
    /**  
     * 构造基于秘钥认证的sftp对象 
     */  
    public SFTPUtil(String username, String host, int port, String privateKey) {  
        this.username = username;  
        this.host = host;  
        this.port = port;  
        this.privateKey = privateKey;  
    }  
    
    public SFTPUtil(){}  
    
    
    /** 
     * 连接sftp服务器 
     */  
    public void login() throws JSchException{  
    	JSch jsch = new JSch();  
        if (privateKey != null) {  
            jsch.addIdentity(privateKey);// 设置私钥  
        }  

        session = jsch.getSession(username, host, port);  
       
        if (password != null) {  
            session.setPassword(password);    
        }  
        Properties config = new Properties();  
        config.put("StrictHostKeyChecking", "no");  
            
        session.setConfig(config);  
        session.connect();  
          
        Channel channel = session.openChannel("sftp");  
        channel.connect();  

        sftp = (ChannelSftp) channel;  
        log.info("登录STFP服务器成功 "+host+":"+port);
    }    
    
    /** 
     * 关闭连接 server  
     */  
    public void logout(){  
        if (sftp != null) {  
            if (sftp.isConnected()) {  
                sftp.disconnect();  
                sftp.exit();
            }  
        }  
        if (session != null) {  
            if (session.isConnected()) {  
                session.disconnect();  
            }  
        } 
        log.info("退出STFP服务器 "+host+":"+port);
    }  

    /**
     * 输出当前路径
     * @return
     * @throws SftpException
     */
    public String pwd() throws SftpException{
    	return sftp.pwd();
    }
    
    /**
     * 进入文件夹
     * @param directory
     * @throws SftpException
     */
    public void cd(String directory) throws SftpException{
    	//如果以“/”开头，则认为是从根目录开始，需要先进入根目录
		if(directory.startsWith("/"))
		{
			sftp.cd("/");
		}
		sftp.cd(directory);
		log.info("进入STFP服务器 "+host+":"+port+" 目录"+directory);
    }
    
    /**
     * 在服务器上创建目录，并直接进入
     * @param directory
     * @throws SftpException
     */
    public void cdAndCreateDirectory(String directory) throws SftpException
    {
    	if(directory != null && !"".equals(directory))
    	{
    		//如果以“/”开头，则认为是从根目录开始，需要先进入根目录
    		if(directory.startsWith("/"))
    		{
    			cd("/");
    		}
    		String [] dirs=directory.split("/");
    		for(String dir:dirs){
            	if(null== dir || "".equals(dir.trim())) continue;
            	dir = dir.trim();
            	try{ 
            		cd(dir);
            	}catch(SftpException ex){
            		sftp.mkdir(dir);
            		cd(dir);
            	}
            }
    	}
    }
    
    /**  
     * 将输入流的数据上传到sftp作为文件，默认当前目录
     * @param sftpFileName  sftp端文件名  
     * @param in   输入流  
     * @param isCreateDir 没有对应文件夹时是否创建
     */  
    public boolean upload(String sftpFileName, InputStream input){  
    	boolean isUpload = false;
        	try {
				sftp.put(input, sftpFileName);//上传文件
				isUpload = true;
				log.info("上传文件完成，"+host+":"+port+" "+pwd()+"/"+sftpFileName);
			} catch (SftpException e) {
				e.printStackTrace();
				isUpload = false;
			}  
        return isUpload;
    } 
    
    /**  
     * 将输入流的数据上传到sftp作为文件。
     * @param directory  上传到该目录  
     * @param sftpFileName  sftp端文件名  
     * @param in   输入流  
     * @param isCreateDir 没有对应文件夹时是否创建
     */  
    public boolean upload(String directory, String sftpFileName, InputStream input,boolean isCreateDir) throws SftpException{  
    	boolean isUpload = false;
    	boolean isHavDir = false;
    	if(directory != null && !"".equals(directory))
    	{
    		try {   
                cd(directory); 
                isHavDir = true;
            } catch (SftpException e) { 
                //目录不存在，是否创建文件夹
            	if(isCreateDir)
            	{
            		cdAndCreateDirectory(directory);
                    isHavDir = true;
            	}
            } 
    	}else{
    		isHavDir = true;
    	}
    	 
        if(isHavDir)
        {
        	isUpload = upload(sftpFileName, input);
        }
        return isUpload;
    } 
    
    
    /**  
     * 将本地文件上传到sftp作为文件。
     * @param directory  上传到该目录  
     * @param sftpFileName  sftp端文件名  
     * @param uploadFile   本地文件  
     * @param isCreateDir 没有对应文件夹时是否创建
     */  
    public boolean upload(String directory, String sftpFileName, String uploadFile,boolean isCreateDir) throws Exception{  
    	boolean isUpload = false;
    	File file = new File(uploadFile);
    	InputStream is = null;
    	try {
			is = new FileInputStream(file);
			isUpload = upload(directory, sftpFileName, is, isCreateDir);
		} catch (FileNotFoundException e) {
			throw new Exception("需要上传的文件不存在："+uploadFile);
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
    	return isUpload;
    }
    
    public void move(String fromDirectoryFile,String toDirectory, String toFileName,boolean isCreateDir) throws Exception
    {
    	boolean isHavDir = false;
    	if(toDirectory != null && !"".equals(toDirectory))
    	{
    		try {   
                cd(toDirectory); 
                isHavDir = true;
            } catch (SftpException e) { 
                //目录不存在，是否创建文件夹
            	if(isCreateDir)
            	{
            		cdAndCreateDirectory(toDirectory);
                    isHavDir = true;
            	}
            } 
    	}else{
    		isHavDir = true;
    	}
    	if(isHavDir)
    	{
        	sftp.rename(fromDirectoryFile, toFileName);
        	log.info("移动文件完成，文件"+fromDirectoryFile+"移动到"+toDirectory+"目录"+toFileName);
    	}else{
    		throw new Exception("移动文件失败，路径不存在");
    	}
    }
    

    /** 
     * 下载文件。
     * @param directory 下载目录  
     * @param downloadFile 下载的文件 
     * @param saveFile 存在本地的路径 
     */    
    public void download(String directory, String downloadFile, String saveFile) throws Exception {  
    	File file = new File(saveFile);  
        OutputStream os = null;
    	try {
			if (directory != null && !"".equals(directory)) {  
			    cd(directory);  
			}  
			os = new FileOutputStream(file);
			sftp.get(downloadFile, os);  
			os.close();
			os = null;
			log.info("下载文件完成，从"+host+":"+port+" "+directory+"/"+downloadFile+"下载到本机"+file.getAbsolutePath());
		} catch (SftpException | IOException e) {
			throw e;
		}finally{
			if(os != null)
			{
				os.close();
			}
		}
    }  
    
    /**  
     * 下载文件流 
     * @param directory 下载目录 
     * @param downloadFile 下载的文件名 
     * @return 文件流  
     */  
    public InputStream download(String directory, String downloadFile) throws SftpException, IOException{  
        if (directory != null && !"".equals(directory)) {  
            cd(directory);  
        }  
        return sftp.get(downloadFile);
    }  
    
    
    /** 
     * 删除文件 
     * @param directory 要删除文件所在目录 
     * @param deleteFile 要删除的文件 
     */  
    public void delete(String directory, String deleteFile) throws SftpException{  
        cd(directory);  
        sftp.rm(deleteFile);  
    }  
    
    /**
     * 目录下所有文件名称，不包含目录名
     * @param directory 目录路径
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public List<String> listFileNames(String directory) throws Exception {
		List<String> fileNameList = new ArrayList<String>();
		Vector<LsEntry> fileList = sftp.ls(directory);
		Iterator<LsEntry> it = fileList.iterator();
		String fileName;
		while (it.hasNext()) {
			fileName = it.next().getFilename();
			if (".".equals(fileName) || "..".equals(fileName)) {
				continue;
			}
			fileNameList.add(fileName);
		}
		return fileNameList;
	}
    
    /**
     * 目录下所有可匹配正则的文件名称，不包含目录名
     * @param directory 目录路径
     * @param regex 正则表达式
     * @return
     * @throws Exception
     */
	public List<String> listFileNames(String directory,String regex) throws Exception {
    	
    	List<LsEntry> rptFiles = listFiles(directory, regex);
		List<String> fileNameList = new ArrayList<String>();
		Iterator<LsEntry> it = rptFiles.iterator();
		String fileName;
		LsEntry entry;
		SftpATTRS attrs;
		while (it.hasNext()) {
			entry = it.next();
			attrs = entry.getAttrs();
			if(!attrs.isDir() && !attrs.isLink())
			{
				fileName = entry.getFilename();
				if (".".equals(fileName) || "..".equals(fileName)) {
					continue;
				}
				fileNameList.add(fileName);
			}
		}
		return fileNameList;
	}
    
	/**
     * 目录下所有可匹配正则的文件LsEntry，不包含目录
     * @param directory 目录路径
     * @param regex 正则表达式
     * @return
     * @throws Exception
     */
	public List<LsEntry> listFiles(String directory,String regex) throws Exception {
    	
    	final Pattern fileTypeMatch = Pattern.compile(regex);
		final List<LsEntry> rptFiles = new ArrayList<LsEntry>();
		
		//新文件定义，某时间点以后
//		String latestMTime = "2019-01-24 23:59:59";
//		final String index = latestMTime;
		
		LsEntrySelector selector = new LsEntrySelector() {
			@Override
			public int select(LsEntry entry) {
				Matcher mtc = fileTypeMatch.matcher(entry.getFilename());
				SftpATTRS attrs = entry.getAttrs();
				boolean isMatch = mtc.find() && !attrs.isDir() && !attrs.isLink();
//				boolean isNewFile = index.compareTo(String.valueOf(attrs.getMTime())) < 0;
//				if (isMatch && isNewFile) {
				if (isMatch) {
					rptFiles.add(entry);
				}
				return CONTINUE;
			}
		};
		
		sftp.ls(directory,selector);
		
		return rptFiles;
	}
    
      
    //上传文件测试
    public static void main(String[] args) throws SftpException, IOException,JSchException {  
        SFTPUtil sftp = new SFTPUtil("user", "user@987", "10.193.1.250", 22);  
        sftp.login(); 
        //进入文件目录
        //sftp.cd("/home/user/Data/Gps/incr");
        
        //文件名称列表
        try {
        	List<String> list = sftp.listFileNames("/home/user/Data/Gps/incr", "^gpsInfo(.*)\\.dat$");
        	if(list != null && list.size() > 0)
        	{
        		for(String string:list)
        		{
        			System.out.println(string);
        		}
        		System.out.println("===================== "+list.size());
        	}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//        try {
//			sftp.download("/usr/local/wiseda", "nginx-1.8.0.tar.gz", "nginx-1.8.0.tar.gz");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        
//        List<String> names = null;
//        try {
//			names = sftp.listFileNames(sftp.pwd(), "^*(.tar.gz|.tar.TXT|.tar.txt)$");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        if(names != null)
//        {
//        	for(String name:names)
//        	{
//        		System.out.println(name);
//        	}
//        	System.out.println("=======names size ："+names.size());
//        }
        
//        try {
//			System.out.println(sftp.upload("222", "111.txt", "D:/111/111.txt", true));;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
       // sftp.download("/usr/local/wiseda", "nginx-1.8.0.tar.gz", "D:/111/nginx-1.8.0.tar.gz");
//        File file = new File("D:\\图片\\t0124dd095ceb042322.jpg");  
//        InputStream is = new FileInputStream(file);  
//          
//        sftp.upload("基础路径","文件路径", "test_sftp.jpg", is);  
        sftp.logout();  
    }  
}