package com.example.test.utils;

import com.sun.crypto.provider.SunJCE;
import org.bouncycastle.util.encoders.Base64;
import sun.security.provider.Sun;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

public class PasswordUtil {
	/**
	 * 加密
	 * @param paramString
	 * @return
	 */
	public static String encryptPassword(String paramString)
	  {
	    try
	    {
	      Key localKey = o00000();
	      Cipher localCipher = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
	      localCipher.init(1, localKey);
	      byte[] arrayOfByte = localCipher.doFinal(paramString.getBytes("UTF-8"));
	      return new String(Base64.encode(arrayOfByte), "UTF-8");
	    }
	    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
	    {
	      throw new RuntimeException(localNoSuchAlgorithmException);
	    }
	    catch (NoSuchPaddingException localNoSuchPaddingException)
	    {
	      throw new RuntimeException(localNoSuchPaddingException);
	    }
	    catch (Exception localException)
	    {
	      throw new RuntimeException(localException);
	    }
	  }

	  /**
	   * 解密
	   * @param paramString
	   * @return
	   */
	  public static String decryptPassword(String paramString)
	  {
	    try
	    {
	      Key localKey = o00000();
	      Cipher localCipher = Cipher.getInstance("DESEDE/ECB/PKCS5Padding");
	      localCipher.init(2, localKey);
	      byte[] arrayOfByte = localCipher.doFinal(Base64.decode(paramString.getBytes("UTF-8")));
	      return new String(arrayOfByte, "UTF-8");
	    }
	    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
	    {
	      throw new RuntimeException(localNoSuchAlgorithmException);
	    }
	    catch (NoSuchPaddingException localNoSuchPaddingException)
	    {
	      throw new RuntimeException(localNoSuchPaddingException);
	    }
	    catch (Exception localException)
	    {
	      throw new RuntimeException(localException);
	    }
	  }

	  private static Key o00000()
	  {
	    try
	    {
	      Security.addProvider(new Sun());
	      SecureRandom localSecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
	      localSecureRandom.setSeed("seed".getBytes("UTF-8"));
	      Security.addProvider(new SunJCE());
	      KeyGenerator localKeyGenerator = KeyGenerator.getInstance("DESEDE", "SunJCE");
	      localKeyGenerator.init(168, localSecureRandom);
	      return localKeyGenerator.generateKey();
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return null;
	  }
	
	public static void main(String[] args) {
		String en = PasswordUtil.encryptPassword("4ce2ae839ba680c082efa7048e134464");
		System.out.println(en);
		System.out.println(PasswordUtil.decryptPassword(en));
	}
}
