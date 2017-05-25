package br.gov.mec.aghu.core.seguranca;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class HashGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3055061488511206133L;

	private BaseEncoderEnum baseEncoder;
	private Charset charSet;

	/**
	 * BaseEncoder default value Base64<br/>
	 * CharSet default value null<br/>
	 * 
	 */
	public HashGenerator() {
		this.setBaseEncoder(BaseEncoderEnum.BASE64);
		// this.setCharSet(Charset.forName("UTF-8"));
	}

	public HashGenerator(BaseEncoderEnum base, Charset cs) {
		this.setBaseEncoder(base);
		this.setCharSet(cs);
	}

	public HashGenerator(BaseEncoderEnum base) {
		this.setBaseEncoder(base);
	}

	public HashGenerator(Charset cs) {
		this();
		this.setCharSet(cs);
	}

	public String generateMD5(String message) throws HashGenerationException {
		return hashString(message, "MD5");
	}

	public String generateSHA1(String message) throws HashGenerationException {
		return hashString(message, "SHA-1");
	}

	public String generateSHA(String message) throws HashGenerationException {
		return hashString(message, "SHA");
	}

	public String generateSHA256(String message) throws HashGenerationException {
		return hashString(message, "SHA-256");
	}
	
	public String generateSSHA(String message) throws HashGenerationException {
		return hashString(message, "SSHA");
	}

	// public String generateMD5base64(String message) throws
	// HashGenerationException {
	// try {
	// MessageDigest digest = MessageDigest.getInstance("MD5");
	// digest.update(message.getBytes());
	//
	// byte[] hashedBytes = digest.digest();
	// String base64 = new BASE64Encoder().encode(hashedBytes);
	//
	// return base64;
	// } catch (NoSuchAlgorithmException e) {
	// throw new RuntimeException(e);
	// }
	// }

	private String hashString(String message, String algorithm) throws HashGenerationException {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			byte[] bytesCharSet;
			if (getCharSet() != null) {
				bytesCharSet = message.getBytes(getCharSet());
			} else {
				bytesCharSet = message.getBytes();
			}
			byte[] hashedBytes = digest.digest(bytesCharSet);

			if (this.getBaseEncoder() == BaseEncoderEnum.BASE64) {
				return convertByteArrayToBase64String(hashedBytes);
			} else if (this.getBaseEncoder() == BaseEncoderEnum.HEX) {
				return convertByteArrayToHexString(hashedBytes);
			} else {
				throw new UnsupportedOperationException("BaseEncoder invalid: " + this.getBaseEncoder());
			}
		} catch (NoSuchAlgorithmException ex) {
			throw new HashGenerationException("Could not generate hash from String", ex);
		} catch (Exception ex) {
			throw new HashGenerationException("Could not generate hash from String", ex);
		}
	}

	private String convertByteArrayToHexString(byte[] arrayBytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++) {
			stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}

	private String convertByteArrayToBase64String(byte[] arrayBytes) {
		return new BASE64Encoder().encode(arrayBytes);
	}

	private BaseEncoderEnum getBaseEncoder() {
		return baseEncoder;
	}

	private void setBaseEncoder(BaseEncoderEnum baseEncoder) {
		this.baseEncoder = baseEncoder;
	}

	public Charset getCharSet() {
		return charSet;
	}

	public void setCharSet(Charset charSet) {
		this.charSet = charSet;
	}
	
}
