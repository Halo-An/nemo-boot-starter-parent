package com.jimistore.boot.nemo.security.helper;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.jimistore.boot.nemo.security.api.exception.TokenInvalidException;

public class TokenFactory implements ITokenFactory {
	
	private static final String SIGN_TYPE = "RSA";
	
	@Value("${token.appid:}")
	String appid;
	
	@Value("${token.appid.ignored:false}")
	boolean appIgnored;
	
	@Value("${token.encrypt-type:HMAC256}")
	String encryptType;
	
	@Value("${token.secret:UW8lRA1rP6JX9oVP7KmlbPGaBW8rPwVv}")
	String secret;
	
	@Value("${token.public-key:}")
	String publicKey;
	
	@Value("${token.private-key:}")
	String privateKey;
	
	@Value("${token.timeout:1800000}")
	int timeout;
	
	class MyRSAKeyProvider implements RSAKeyProvider{
		
		RSAPublicKey rsaPublicKey;
		
		RSAPrivateKey rsaPrivateKey;

		public MyRSAKeyProvider(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
			super();
			this.rsaPublicKey = rsaPublicKey;
			this.rsaPrivateKey = rsaPrivateKey;
		}

		@Override
		public RSAPublicKey getPublicKeyById(String keyId) {
			return rsaPublicKey;
		}

		@Override
		public RSAPrivateKey getPrivateKey() {
			return rsaPrivateKey;
		}

		@Override
		public String getPrivateKeyId() {
			return rsaPrivateKey.toString();
		}
		
	}
	
	/**
	 * 获取公私钥
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	private RSAKeyProvider getRSAKeyProvider(){
		try{
	        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(SIGN_TYPE);
	        keyPairGen.initialize(1024);
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        
	        return new MyRSAKeyProvider((RSAPublicKey)keyPair.getPublic(), (RSAPrivateKey)keyPair.getPrivate());
		}catch(Exception e){
			throw new RuntimeException("check public key and private key error");
		}
				
	}
	
	private Algorithm getAlgorithm(){
		Algorithm algorithm = null;
		if(encryptType.equals("RSA256")){
			algorithm = Algorithm.RSA256(this.getRSAKeyProvider());
		}else{
			try {
				algorithm = Algorithm.HMAC256(secret);
			} catch (IllegalArgumentException | UnsupportedEncodingException e) {
				throw new RuntimeException("genner token faild");
			}
		}
		return algorithm;
	}

	@Override
	public String create(String userId, String deviceId) {
		Calendar expired = Calendar.getInstance();
		expired.add(Calendar.MILLISECOND, this.timeout);
		return JWT.create().withSubject(userId).withKeyId(deviceId).withIssuer(appid).withExpiresAt(expired.getTime()).sign(getAlgorithm());
	}

	@Override
	public String create(String appid, String userId, String deviceId, int timeout) {
		Calendar expired = Calendar.getInstance();
		expired.add(Calendar.MILLISECOND, timeout);
		JWTCreator.Builder builder = JWT.create().withSubject(userId).withKeyId(deviceId).withIssuer(appid).withExpiresAt(expired.getTime());
		return builder.sign(getAlgorithm());
	}

	@Override
	public String create(String appid, String userId, String deviceId, Map<String, String> extend, int timeout) {
		Calendar expired = Calendar.getInstance();
		expired.add(Calendar.MILLISECOND, timeout);
		JWTCreator.Builder builder = JWT.create().withSubject(userId).withKeyId(deviceId).withIssuer(appid).withExpiresAt(expired.getTime());
		if(extend!=null&&extend.size()>0){
			for(Entry<String,String> entry:extend.entrySet()){
				builder.withClaim(entry.getKey(), entry.getValue());
			}
		}
		return builder.sign(getAlgorithm());
	}

	@Override
	public void check(String userId, String deviceId, String token) throws TokenInvalidException {
		if(userId==null||deviceId==null||token==null){
			throw new TokenInvalidException("param of token cannot be empty");
		}
		
		DecodedJWT jwt = null;
		try{
			jwt = JWT.decode(token);
		}catch(JWTDecodeException ex){
			throw new TokenInvalidException("decode token faild");
		}
		
		if(jwt.getExpiresAt()==null||jwt.getExpiresAt().getTime()<System.currentTimeMillis()){
			throw new TokenInvalidException("token has expired");
		}
		
		if(jwt.getSubject()==null||!jwt.getSubject().equals(userId)){
			throw new TokenInvalidException("check userId of token faild");
		}
		
		if(jwt.getKeyId()==null||!jwt.getKeyId().equals(deviceId)){
			throw new TokenInvalidException("check deviceId of token faild");
		}
		
		if(appid!=null&&appid.trim().length()>0&&!appIgnored){
			
			if(jwt.getIssuer()==null||!jwt.getIssuer().equals(appid)){
				throw new TokenInvalidException("token has expired");
			}
		}
	}
	
	

}
