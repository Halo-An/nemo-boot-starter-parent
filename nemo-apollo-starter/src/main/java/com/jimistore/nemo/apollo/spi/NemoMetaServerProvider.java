package com.jimistore.nemo.apollo.spi;

import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.core.spi.MetaServerProvider;
import com.jimistore.nemo.apollo.enums.MetaAddressEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义metaServer获取逻辑
 *
 * @author hexin
 */
public class NemoMetaServerProvider implements MetaServerProvider {

    private static final Logger logger = LoggerFactory.getLogger(NemoMetaServerProvider.class);

    public static final int ORDER = -1;
    private static final Map<Env, String> domains = new HashMap<>();

    public NemoMetaServerProvider(){
        initialize();
    }

    private void initialize(){
        domains.put(Env.LOCAL, MetaAddressEnum.LOCAL.getAddress());
        domains.put(Env.DEV, MetaAddressEnum.DEV.getAddress());
        domains.put(Env.TEST, MetaAddressEnum.TEST.getAddress());
        domains.put(Env.TEST2, MetaAddressEnum.TEST2.getAddress());
        domains.put(Env.EMER, MetaAddressEnum.EMER.getAddress());
        domains.put(Env.SANDBOX, MetaAddressEnum.SANDBOX.getAddress());
        domains.put(Env.PROD, MetaAddressEnum.PROD.getAddress());
    }

    @Override
    public String getMetaServerAddress(Env env) {
        String metaServerAddress = domains.get(env);
        if (metaServerAddress==null){
            logger.warn("could not find meta server address from NemoMetaServerProvider, env:{}", env);
        }else {
            metaServerAddress = metaServerAddress.trim();
        }
        return metaServerAddress;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
