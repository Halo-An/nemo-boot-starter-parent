package com.jimistore.boot.nemo.security.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.jimistore.boot.nemo.security.api.helper.IApiAuth;
import com.jimistore.boot.nemo.security.helper.ApiAuth;
import com.jimistore.boot.nemo.security.helper.ITokenFactory;
import com.jimistore.boot.nemo.security.helper.RequestProxyFilter;
import com.jimistore.boot.nemo.security.helper.SignatureValidateAspect;
import com.jimistore.boot.nemo.security.helper.TokenFactory;
import com.jimistore.boot.nemo.security.helper.TokenValidateAspect;
import com.jimistore.util.format.string.StringUtil;

@Configuration
public class NemoSecurityAutoConfiguration implements EnvironmentAware {
	
    private Map<String,ApiAuth.ApiAuthConfig> secretMap=new HashMap<String,ApiAuth.ApiAuthConfig>();
    
	private void setSignatureConfig(Environment environment){
		Map<String,Map<String, Object>> map = this.parseEnv(environment, "auth", "appids");
		
		if(map==null){
			throw new RuntimeException("can not find config of security");
		}
		
		for (Entry<String,Map<String, Object>> entry : map.entrySet()) {
			String dsPrefix = entry.getKey();
			Map<String, Object> dsMap = entry.getValue();
			try{
			secretMap.put(dsPrefix, new ApiAuth.ApiAuthConfig()
					.setAppid(dsPrefix)
					.setSecret(dsMap.get("secret").toString())
					.setMatch(StringUtil.split(dsMap.get("match").toString(), StringUtil.SPLIT_STR)));
			}catch(Exception e){
				
			}
		}
	}
	
	@Override
	public void setEnvironment(Environment environment) {
		this.setSignatureConfig(environment);
	}
	
	private Map<String,Map<String, Object>> parseEnv(Environment environment, String prefix, String nameKey){
		Map<String,Map<String, Object>> map = new HashMap<String,Map<String, Object>>();
		
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
				environment, String.format("%s.", prefix));
		String dsPrefixs = propertyResolver.getProperty(nameKey);
		if(dsPrefixs==null){
			return null;
		}
		String[] dsPrefixsArr = dsPrefixs.split(",");
		if(dsPrefixsArr==null){
			return null;
		}
		for (String dsPrefix : dsPrefixsArr) {
			Map<String, Object> dsMap = propertyResolver.getSubProperties(String.format("%s.", dsPrefix));
			if(dsMap==null){
				continue;
			}
			map.put(dsPrefix, dsMap);
		}
		
		return map;
	}
	

	
	@Bean
	@ConditionalOnMissingBean(IApiAuth.class)
	public ApiAuth ApiAuth(){
		ApiAuth apiAuthHelper = new ApiAuth();
		if(secretMap!=null&&!secretMap.isEmpty()){
			apiAuthHelper.setSecretMap(secretMap);
		}
		return apiAuthHelper;
	}
	
	@Bean
	@ConditionalOnMissingBean(SignatureValidateAspect.class)
	public SignatureValidateAspect SignatureValidateAspect(IApiAuth apiAuth){
		return new SignatureValidateAspect().setApiAuth(apiAuth);
	}
	
	@Bean
	@ConditionalOnMissingBean(ITokenFactory.class)
	public TokenFactory ITokenFactory(){
		return new TokenFactory();
	}
	
	@Bean
	@ConditionalOnMissingBean(TokenValidateAspect.class)
	public TokenValidateAspect TokenValidateAspect(ITokenFactory tokenFactory){
		return new TokenValidateAspect().setTokenFactory(tokenFactory);
	}
	
	@Bean
	public RequestProxyFilter RequestProxyFilter(){
		return new RequestProxyFilter();
	}

}