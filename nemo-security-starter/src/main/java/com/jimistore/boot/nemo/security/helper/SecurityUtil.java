package com.jimistore.boot.nemo.security.helper;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

public class SecurityUtil {

	/**
	 * 获取签名的内容
	 * 
	 * @param map
	 * @return
	 */
	public static String getSignSource(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		// 清除空的值
		Iterator<String> keyIt = map.keySet().iterator();
		while (keyIt.hasNext()) {
			String key = keyIt.next();
			Object value = map.get(key);
			if (value == null) {
				keyIt.remove();
				map.remove(key);
			}
		}
		// 组成数组
		String[] items = new String[map.size()];
		StringBuilder source = new StringBuilder();
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			items[i] = String.format("%1$s=%2$s", entry.getKey(), entry.getValue());
			i++;
		}
		// 排序
		Arrays.sort(items);
		// 拼接字符串
		for (String item : items) {
			if (source.length() > 0) {
				source.append("&");
			}
			source.append(item);
		}
		return source.toString();
	}

	/**
	 * MD5签名加密
	 * 
	 * @param map
	 * @return
	 * @throws SignatureInvalidException
	 */
	public static String SignMD5(Map<String, Object> srcMap) throws SignatureInvalidException {
		String source = SecurityUtil.getSignSource(srcMap);
		if (srcMap == null || srcMap.size() == 0) {
			throw new SignatureInvalidException("签名的参数不能为空");
		}
		try {
			// MD5
			return MD5Util.Bit32(source).toLowerCase();
		} catch (Exception e) {
			throw new SignatureInvalidException(e);
		}

	}

	/**
	 * 使用RSA公钥加密数据
	 * 
	 * @param pubKeyInByte 打包的byte[]形式公钥
	 * @param data         要加密的数据
	 * @return 加密数据
	 */
	public static byte[] encryptByRSA(byte[] pubKeyInByte, byte[] data) {
		try {
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
			PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 用RSA私钥解密
	 * 
	 * @param privKeyInByte 私钥打包成byte[]形式
	 * @param data          要解密的数据
	 * @return 解密数据
	 */
	public static byte[] decryptByRSA(byte[] privKeyInByte, byte[] data) {
		try {
			PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(privKeyInByte);
			KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			return null;
		}

	}

}
