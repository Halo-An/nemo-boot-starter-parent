package com.jimistore.boot.nemo.security.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.jimistore.boot.nemo.security.core.ISignatureValidator;
import com.jimistore.boot.nemo.security.core.SignatureDefaultValidator;
import com.jimistore.boot.nemo.security.core.SignatureMD5Validator;
import com.jimistore.boot.nemo.security.core.SignatureValidatorV1;
import com.jimistore.boot.nemo.security.core.SignatureValidatorV2;
import com.jimistore.boot.nemo.security.helper.ApiAuth;
import com.jimistore.boot.nemo.security.helper.IApiAuth;
import com.jimistore.boot.nemo.security.helper.ITokenFactory;
import com.jimistore.boot.nemo.security.helper.SignatureValidateAspectV1;
import com.jimistore.boot.nemo.security.helper.SignatureValidateAspectV2;
import com.jimistore.boot.nemo.security.helper.SignatureValidateFilter;
import com.jimistore.boot.nemo.security.helper.StringUtil;
import com.jimistore.boot.nemo.security.helper.TokenFactory;
import com.jimistore.boot.nemo.security.helper.TokenValidateAspect;

@Configuration
public class NemoSecurityAutoConfiguration implements EnvironmentAware {

	private Map<String, ApiAuth.ApiAuthConfig> secretMap = new HashMap<String, ApiAuth.ApiAuthConfig>();

	private void setSignatureConfig(Environment environment) {
		Map<String, Map<Object, Object>> map = this.parseEnv(environment, "auth", "appids");

		if (map != null) {
			for (Entry<String, Map<Object, Object>> entry : map.entrySet()) {
				String dsPrefix = entry.getKey();
				Map<Object, Object> dsMap = entry.getValue();
				try {
					String secret = String.class.cast(dsMap.get("secret"));
					String match = String.class.cast(dsMap.get("match"));
					secretMap.put(dsPrefix,
							new ApiAuth.ApiAuthConfig().setAppid(dsPrefix)
									.setSecret(secret)
									.setMatch(StringUtil.split(match, StringUtil.SPLIT_STR)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void setEnvironment(Environment environment) {
		this.setSignatureConfig(environment);
	}

	private Map<String, Map<Object, Object>> parseEnv(Environment environment, String prefix, String nameKey) {

		// 现在的code:
		Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
		Binder binder = new Binder(sources);
		String dsPrefixs = binder.bind(String.format("%s.%s", prefix, nameKey), String.class).get();

		if (dsPrefixs == null) {
			return null;
		}
		String[] dsPrefixsArr = dsPrefixs.split(",");
		if (dsPrefixsArr == null) {
			return null;
		}

		Map<String, Map<Object, Object>> map = new HashMap<>();
		for (String dsPrefix : dsPrefixsArr) {
			Map<Object, Object> dsMap = binder.bind(String.format("%s.%s", prefix, dsPrefix), Properties.class).get();

			if (dsMap == null) {
				continue;
			}
			map.put(dsPrefix, dsMap);
		}

		return map;
	}

	@Bean
	@ConditionalOnMissingBean(IApiAuth.class)
	public ApiAuth ApiAuth() {
		ApiAuth apiAuthHelper = new ApiAuth();
		if (secretMap != null && !secretMap.isEmpty()) {
			apiAuthHelper.setSecretMap(secretMap);
		}
		return apiAuthHelper;
	}

	@Bean
	@ConditionalOnMissingBean(SignatureValidateFilter.class)
	public SignatureValidateFilter signatureValidateFilter(Set<ISignatureValidator> signValidatorSet) {
		return new SignatureValidateFilter().setSignValidatorSet(signValidatorSet);
	}

	@Bean
	@ConditionalOnBean({ SignatureValidateFilter.class })
	public SignatureDefaultValidator signatureDefaultValidator(IApiAuth apiAuth) {
		return new SignatureDefaultValidator().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnBean(SignatureValidateFilter.class)
	public SignatureMD5Validator signatureMD5Validator(IApiAuth apiAuth) {
		return new SignatureMD5Validator().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnBean({ SignatureValidateFilter.class })
	public SignatureValidatorV1 signatureValidatorV1(IApiAuth apiAuth) {
		return new SignatureValidatorV1().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnBean({ SignatureValidateFilter.class })
	public SignatureValidatorV2 signatureValidatorV2(IApiAuth apiAuth) {
		return new SignatureValidatorV2().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnMissingBean({ SignatureValidateFilter.class })
	public SignatureValidateAspectV1 signatureValidateAspectV1(IApiAuth apiAuth) {
		return new SignatureValidateAspectV1().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnMissingBean({ SignatureValidateFilter.class })
	public SignatureValidateAspectV2 signatureValidateAspectV2(IApiAuth apiAuth) {
		return new SignatureValidateAspectV2().setApiAuth(apiAuth);
	}

	@Bean
	@ConditionalOnMissingBean(ITokenFactory.class)
	public TokenFactory ITokenFactory() {
		return new TokenFactory();
	}

	@Bean
	@ConditionalOnMissingBean(TokenValidateAspect.class)
	public TokenValidateAspect TokenValidateAspect(ITokenFactory tokenFactory) {
		return new TokenValidateAspect().setTokenFactory(tokenFactory);
	}

}
