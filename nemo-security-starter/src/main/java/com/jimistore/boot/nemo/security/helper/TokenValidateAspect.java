package com.jimistore.boot.nemo.security.helper;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.security.api.exception.TokenInvalidException;
import com.jimistore.boot.nemo.security.api.request.IDeviceAuthRequest;
import com.jimistore.boot.nemo.security.api.request.IUserAuthRequest;
import com.jimistore.util.format.string.StringUtil;


@Aspect
@Order(100)
public class TokenValidateAspect {
	
	private final Logger log = Logger.getLogger(getClass());
	public static final String TOKEN = "token";
	public static final String USERID = "userId";
	public static final String DEVICE = "deviceId";
	public static final int dev = 120;
		
	private String[] ignoreMatchs;
	
	private String[] matchs;
	
	private ITokenFactory tokenFactory;
	
	public TokenValidateAspect setTokenFactory(ITokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
		return this;
	}

	@Value("${token.ignore.match:}")
	public TokenValidateAspect setIgnoreMatchStr(String ignoreMatchStr) {
		this.ignoreMatchs = StringUtil.split(ignoreMatchStr, StringUtil.SPLIT_STR);
		return this;
	}

	@Value("${token.match}")
	public TokenValidateAspect setMatchStr(String matchStr) {
		this.matchs = StringUtil.split(matchStr, StringUtil.SPLIT_STR);
		return this;
	}




	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void auth(){
	}
	
	@Before("auth()")
	public void before(JoinPoint joinPoint) throws Throwable {
        
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
    	String userid = request.getHeader(USERID);
		Context.put(Context.CONTEXT_REQUEST_USER, userid);

		//如果命中忽略策略
		if(this.checkIgnore(request)){
			return ;
		}
		
		this.checkToken(joinPoint, request);
        
       
	}
	
	private boolean checkIgnore(HttpServletRequest request){
		String url = request.getRequestURI().toString();
        AntPathMatcher matcher = new AntPathMatcher();
        
        boolean flag = true;
        //匹配是否是验证范围
        if(matchs!=null){
        	for(String matchStr:matchs){
            	if(matchStr.trim().length()>0&&matcher.match(matchStr, url)){
            		if(log.isDebugEnabled()){
            			log.debug(String.format("hit check strategy, the match is %s, url is %s ", matchStr, url));
            		}
            		flag = false;
            	}
            }
        }
        
        //匹配忽略url
        if(!flag&&ignoreMatchs!=null){
            for(String matchStr:ignoreMatchs){
            	if(matchStr.trim().length()>0&&matcher.match(matchStr, url)){
            		if(log.isDebugEnabled()){
            			log.debug(String.format("hit ignore strategy, the match is %s, url is %s ", matchStr, url));
            		}
            		return true;
            	}
            }
        }
        return flag;
	}
	
	private void checkToken(JoinPoint joinPoint, HttpServletRequest request){
		String device = request.getHeader(DEVICE);
    	String token = request.getHeader(TOKEN);
    	String userid = request.getHeader(USERID);
		
		for(Object arg:joinPoint.getArgs()){
			if(arg instanceof IUserAuthRequest){
				IUserAuthRequest userRequest = (IUserAuthRequest)arg;
				if(userRequest.getUserId()==null||!userRequest.getUserId().equals(userid)){
					throw new TokenInvalidException();
				}
			}else if(arg instanceof IDeviceAuthRequest){
				IDeviceAuthRequest deviceRequest = (IDeviceAuthRequest)arg;
				if(deviceRequest.getDeviceId()==null||!deviceRequest.getDeviceId().equals(device)){
					throw new TokenInvalidException();
				}
			}
		}
    	
		tokenFactory.check(userid, device, token);
				
	}
	
	
}

