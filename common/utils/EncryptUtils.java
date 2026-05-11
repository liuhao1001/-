package myproject.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static cn.hutool.core.lang.Console.log;

@UtilityClass
public class EncryptUtils {
	//=======AES=====
	public static final String sKey = "111;AAAA1234++==";

	//======DESKey=======
	private static Key key;
	private static final String PRIVATE_KEY = "ABC";


	//=======AESUtils=======
	/*
	 * 加密（对外暴露）
	 */
	public static String encryptData(String privateKey, String content) throws Exception {
		KeyGenerator keygen = getKeyGenerator(privateKey);
		SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
		return Base64.getEncoder().encodeToString(encrypt(key, content.getBytes(StandardCharsets.UTF_8)));
	}

	/*
	 * 解密（对外暴露）
	 */
	public static String decryptData(String privateKey, String content) throws Exception {
		KeyGenerator keygen = getKeyGenerator(privateKey);
		SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
		return new String(decrypt(key, Base64.getDecoder().decode(content)), StandardCharsets.UTF_8);
	}

	private static KeyGenerator getKeyGenerator(String privateKey) throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(privateKey.getBytes());
		keygen.init(128, secureRandom);
		return keygen;
	}

	private static byte[] encrypt(Key key, byte[] srcBytes) {
		if (key != null) {
			try {
				// Cipher负责完成加密或解密工作，基于AES
				Cipher cipher = Cipher.getInstance("AES");
				// 对Cipher对象进行初始化
				cipher.init(Cipher.ENCRYPT_MODE, key);
				// 加密，保存并返回
				return cipher.doFinal(srcBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static byte[] decrypt(Key key, byte[] encBytes) {
		if (key != null) {
			try {
				Cipher cipher = Cipher.getInstance("AES");
				//对Cipher对象进行初始化
				cipher.init(Cipher.DECRYPT_MODE, key);
				//解密
				return cipher.doFinal(encBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String AESEncryptData(String content) throws Exception {
		return encryptData(sKey, content);
	}

	public static String AESDecryptData(String content) throws Exception {
		return decryptData(sKey, content);
	}

	public static String AESEncryptData(String privateKey, String content) throws Exception {
		return encryptData(privateKey, content);
	}

	public static String AESDecryptData(String privateKey, String content) throws Exception {
		return decryptData(privateKey, content);
	}

	//=======DESUtils=======

	static {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(PRIVATE_KEY.getBytes());
			generator.init(secureRandom);
			key = generator.generateKey();
			generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加密，返回BASE64的加密字符串
	 *
	 * @param str
	 * @return
	 */
	public static String DESEncryptData(String str) throws Exception {
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptStrBytes = cipher.doFinal(strBytes);
		return Base64.getEncoder().encodeToString(encryptStrBytes);
	}

	/**
	 * 对BASE64加密字符串进行解密
	 *
	 * @param str
	 * @return
	 */
	public static String DESDecryptData(String str) throws Exception {
		byte[] strBytes = Base64.getDecoder().decode(str);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] encryptStrBytes = cipher.doFinal(strBytes);
		return new String(encryptStrBytes, StandardCharsets.UTF_8);
	}

	//=====RSAUtils======


	/**
	 * 加密（对外暴露）
	 * 如果使用 公钥 对数据 进行加密，只有用对应的 私钥 才能 进行解密。
	 * 如果使用 私钥 对数据 进行加密，只有用对应的 公钥 才能 进行解密。
	 *
	 * @param keyStr
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String RSAEncryptData(String keyStr, String data, Boolean isPublicKey) throws Exception {
		if (StringUtils.isEmpty(keyStr)) {
			return "";
		}
		return encryptBASE64(RSAEncrypt(getKey(keyStr, isPublicKey), data.getBytes()));
	}

	/**
	 * 解密（对外暴露）
	 * 如果使用 公钥 对数据 进行加密，只有用对应的 私钥 才能 进行解密。
	 * 如果使用 私钥 对数据 进行加密，只有用对应的 公钥 才能 进行解密。
	 *
	 * @param keyStr
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String RSADecryptData(String keyStr, String data, Boolean isPublicKey) throws Exception {
		if (StringUtils.isEmpty(keyStr)) {
			return "";
		}
		return new String(RSADecrypt(getKey(keyStr, isPublicKey), decryptBASE64(data)), StandardCharsets.UTF_8);
	}

	/**
	 * 加密
	 *
	 * @param key
	 * @param srcBytes
	 * @return
	 */
	private static byte[] RSAEncrypt(Key key, byte[] srcBytes) {
		if (key != null) {
			try {
				//Cipher负责完成加密或解密工作，基于RSA
				Cipher cipher = Cipher.getInstance("RSA");
				//对Cipher对象进行初始化
				cipher.init(Cipher.ENCRYPT_MODE, key);
				//加密，并返回
				return cipher.doFinal(srcBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 解密
	 *
	 * @param key
	 * @param encBytes
	 * @return
	 */
	private static byte[] RSADecrypt(Key key, byte[] encBytes) {
		if (key != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				//对Cipher对象进行初始化
				cipher.init(Cipher.DECRYPT_MODE, key);
				//解密并返回结果
				return cipher.doFinal(encBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据key获取公有或者私有key对象
	 *
	 * @param keyStr
	 * @param isPublicKey
	 * @return
	 * @throws Exception
	 */
	private static Key getKey(String keyStr, Boolean isPublicKey) throws Exception {
		if (isPublicKey) {
			return getPublicKey(keyStr);
		} else {
			return getPrivateKey(keyStr);
		}
	}

	/**
	 * 根据公有key获取公有key对象
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static RSAPublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * 根据私有key获取私有对象
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static RSAPrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 获取公有/私有Key
	 *
	 * @return
	 */
	private static KeyPair getRSAKey() {
		KeyPair keyPair = null;
		try {
			//生成公钥和私钥对，基于RSA算法生成对象
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			//初始化密钥对生成器，密钥大小为1024位
			keyPairGen.initialize(1024);
			//生成一个密钥对，保存在keyPair中
			keyPair = keyPairGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return keyPair;
	}

	/**
	 * 对字符串进行BASE64Decoder
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] decryptBASE64(String key) {
		return Base64.getDecoder().decode(key);
	}

	/**
	 * 对字节数组进行BASE64Encoder
	 *
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static String encryptBASE64(byte[] key) {
		return Base64.getEncoder().encodeToString(key);
	}


	private static AES initAES(SecretKey key) {
		return new AES(key);
	}


	/**
	 * AES 加密
	 *
	 * @param encryptPath
	 * @param decryptPath
	 * @param sKey
	 * @return
	 */
	@SneakyThrows
	public static String encryptFile(String encryptPath, String esKey) {
		KeyGenerator keygen = getKeyGenerator(esKey);
		SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
		File encryptFile = null;
		File decryptfile = null;
		String decodePath = encryptPath + ".decrypt";
		BufferedOutputStream bufferedOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			encryptFile = FileUtil.readFileByPath(encryptPath);
			if (!encryptFile.exists()) {
				throw new NullPointerException("Encrypt file is empty");
			}
			decryptfile = new File(decodePath);
			if (decryptfile.exists()) {
				decryptfile.delete();
			}
			decryptfile.createNewFile();
			bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(decryptfile.toPath()));
			bufferedInputStream = new BufferedInputStream(Files.newInputStream(encryptFile.toPath()));

			AES aes = initAES(key);
			aes.encrypt(bufferedInputStream, bufferedOutputStream, true);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return StrUtil.EMPTY;
		}
		return decodePath;
	}

	/**
	 * AES 解密
	 *
	 * @param encryptFile
	 * @param decryptFile
	 * @param mKey
	 * @return
	 */
	@SneakyThrows
	public static File decryptFile(String encryptFilePath, String mKey) {
		log("解密密钥>>>>>>>>>>>>>>>>>>>>> {}", mKey);
		KeyGenerator keygen = getKeyGenerator(mKey);
		SecretKey key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
		BufferedOutputStream outputStream = null;
		BufferedInputStream inputStream = null;
		//CipherInputStream inputStream = null;
		File decryptFile = new File(encryptFilePath.replaceAll(".decrypt", ""));
		File encryptFile = FileUtil.readFileByPath(encryptFilePath);
		try {
			if (!encryptFile.exists()) {
				throw new NullPointerException("Decrypt file is empty");
			}
			if (decryptFile.exists()) {
				decryptFile.delete();
			}
			decryptFile.createNewFile();
			AES aes = initAES(key);
			outputStream = new BufferedOutputStream(Files.newOutputStream(decryptFile.toPath()));
			inputStream = new BufferedInputStream(Files.newInputStream(encryptFile.toPath()));
			aes.decrypt(inputStream, outputStream, true);
			//解密后文件MD5 值，用于判断与原文件是否一致
			DigestUtil.md5Hex(decryptFile);
		} catch (Exception e) {
			log("解密失败>>>>>>>>{}", e.getMessage());
		} finally {
			log("删除原加密文件>>>>>>>{}", encryptFile.getAbsolutePath());
		}
		return decryptFile;
//		return encryptFilePath.replaceAll(".decrypt", "");
	}


	public static void main(String[] args) throws Exception {
		//AES
		String prisKey = "ABC";
		String content = "ASD456";
		String m = AESEncryptData(prisKey, content);
		System.out.println("根据私钥：" + prisKey + "，加密后的密文是：" + m);
		System.out.println("根据私钥：" + prisKey + "，解密后的明文是：" + AESDecryptData(prisKey, m));
		//DES
		String name = "catdog";
		String password = "Cat<%1?2>Dog";
		String encryname = DESEncryptData(name);
		String encrypassword = DESEncryptData(password);
		System.out.println("加密：" + encryname);
		System.out.println("加密：" + encrypassword);

		System.out.println("解密：" + DESDecryptData(encryname));
		System.out.println("解密：" + DESDecryptData(encrypassword));
// RSA
//		// 生成的一对key保存好
		try {
//			//得到私钥和公钥
			KeyPair keyPair = getRSAKey();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			// 保存好
			String priKey = encryptBASE64(privateKey.getEncoded());
			String pubKey = encryptBASE64(publicKey.getEncoded());

			System.out.println("公钥：" + pubKey);
			System.out.println("私钥：" + priKey);

			pubKey = Base64Utils.encodeToString(sKey.getBytes());
			priKey = Base64Utils.encodeToString("1".getBytes());

			// 测试
			String message = "QWERDF";
			System.out.println("明文：" + message);
			String jiami = RSAEncryptData(pubKey, message, true);
			System.out.println("公钥加密后：" + jiami);
			String jiemi = RSADecryptData(priKey, jiami, false);
			System.out.println("用私钥解密后的结果是:" + jiemi);

			jiami = RSAEncryptData(priKey, message, false);
			System.out.println("私钥加密后：" + jiami);
			jiemi = RSADecryptData(pubKey, jiami, true);
			System.out.println("用公钥解密后的结果是:" + jiemi);


			System.out.println(encryptFile("upload/100v1f000001h1azeFE2F_D_10000_1200_35958.jpg", "123"));
			File file = decryptFile("upload/100v1f000001h1azeFE2F_D_10000_1200_35958.jpg.decrypt", "123");
//			response.reset();
//			response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
//			response.addHeader("Content-Length", "" + file.length());
//			response.setContentType("application/octet-stream; charset=UTF-8");
//			IoUtil.write(response.getOutputStream(), false, cn.hutool.core.io.FileUtil.readBytes(file));

		} catch (Exception e) {
			e.printStackTrace();
		}
		//文件加解密  是否需要RSA
	}

}
