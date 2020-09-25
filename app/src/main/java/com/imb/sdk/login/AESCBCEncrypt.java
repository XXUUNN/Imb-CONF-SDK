package com.imb.sdk.login;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCBCEncrypt {
	
	private static AESCBCEncrypt aes = null;
	private final String PKEY = "microsys12345678"; 
	
	public static byte[] key = new byte[] { 109,105,99,114,111,115,121,115,49,50,51,52,53,54,55,56 };//microsys12345678

	private AESCBCEncrypt() {

	}

	public static synchronized AESCBCEncrypt getInstance() {
		if (aes == null) {
			aes = new AESCBCEncrypt();
		}
		return aes;
	}

	public String encrypt(String msg) {

		String str = "";
		try {
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(key);
			SecretKeySpec key2 = new SecretKeySpec(PKEY.getBytes(), "AES");
			Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, key2, paramSpec);
			str = asHex(ecipher.doFinal(msg.getBytes()));
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return str;
	}

	public String decrypt(String value) {
		try {
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(key);
			SecretKeySpec key2 = new SecretKeySpec(PKEY.getBytes(), "AES");
			Cipher dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher.init(Cipher.DECRYPT_MODE, key2, paramSpec);
			return URLDecoder.decode(new String(dcipher.doFinal(asBin(value))), "UTF-8");
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String asHex(byte[] buf) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}

		return strbuf.toString();
	}

	private byte[] asBin(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
			
			String aes = src.substring(i * 2, i * 2 + 1);
			int high = Integer.parseInt(aes, 16);
			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
			encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	}

	public static void main(String[] args) {
//		String str1 = "microsys";
//		System.out.println(str1.length());
//		String str = AESCBCEncrypt.getInstance().encrypt(str1); 
//		System.out.println("加密后的值为：");
//		System.out.println(str);
//		System.out.println(str.length());
//		System.out.println("解密后的值为：");
//		System.out.println(AESCBCEncrypt.getInstance().decrypt(str));
	}
}
