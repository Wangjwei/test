/**
 * 
 */
package com.example.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("commonLaunch")
public class Launch {
	
	public static Launch launch = null;
	
	@Value("${common.default.password}")
	private String defaultPassword;
	
	@Value("${common.icons.path}")
	private String iconsPath;

	@Value("${common.auth.path}")
	private String authPath;
	

	@Value("${file.upload.path}")
	private String fileUploadPath;

	@Value("${file.view.path}")
	private String fileViewPath;
	


	/**
	 * @return the fileUploadPath
	 */
	public String getFileUploadPath() {
		return fileUploadPath;
	}

	/**
	 * @param fileUploadPath the fileUploadPath to set
	 */
	public void setFileUploadPath(String fileUploadPath) {
		this.fileUploadPath = fileUploadPath;
	}

	
	/**
	 * @return the fileViewPath
	 */
	public String getFileViewPath() {
		return fileViewPath;
	}

	/**
	 * @param fileViewPath the fileViewPath to set
	 */
	public void setFileViewPath(String fileViewPath) {
		this.fileViewPath = fileViewPath;
	}

	/**
	 * @return the defaultPassword
	 */
	public String getDefaultPassword() {
		return defaultPassword;
	}

	/**
	 * @param defaultPassword the defaultPassword to set
	 */
	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	/**
	 * @return the iconsPath
	 */
	public String getIconsPath() {
		return iconsPath;
	}

	/**
	 * @param iconsPath the iconsPath to set
	 */
	public void setIconsPath(String iconsPath) {
		this.iconsPath = iconsPath;
	}

	/**
	 * @return the authPath
	 */
	public String getAuthPath() {
		return authPath;
	}

	/**
	 * @param authPath the authPath to set
	 */
	public void setAuthPath(String authPath) {
		this.authPath = authPath;
	}

	private Launch (){
		super();
	}

	@PostConstruct
    public void init() {
		launch = this;
    }

	
	/**
	 * 
	 * @return
	 */
	public static synchronized Launch newInstance(){
		if(launch == null){
			launch = new Launch();
		}
		return launch;
	}
}
