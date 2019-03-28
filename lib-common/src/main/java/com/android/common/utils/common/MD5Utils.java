package com.android.common.utils.common;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	/**
	 * 将密码进行md5加密
	 * 
	 * @param password
	 *            明文密码
	 * @return 加密后的密码
	 */
	public static String encode(String password) {

		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				// 获取低八为内容
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用md5对文件进行加密
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static String encodeFile(String path) {

		try {

			MessageDigest digest = MessageDigest.getInstance("MD5");

			FileInputStream fis = new FileInputStream(path);
			int len = 0;
			byte[] bys = new byte[1024];
			while ((len = fis.read(bys)) != -1) {
				digest.update(bys, 0, len);
			}

			byte[] bytes = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				// 获取低八为内容
				int i = b & 0xff;
				String hexString = Integer.toHexString(i);
				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
