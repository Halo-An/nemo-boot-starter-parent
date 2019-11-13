package com.jimistore.nemo.apollo.enums;

/**
 * metaAddress枚举
 *
 * @author hexin
 */
public enum MetaAddressEnum {

    /**
     * 不同环境metaAddress
     */
    LOCAL("http://apollo-meta-server.dev.jimistore.com"),
    DEV("http://apollo-meta-server.dev.jimistore.com"),
    TEST("http://apollo-meta-server.test.jimistore.com"),
    TEST2("http://apollo-meta-server.test2.jimistore.com"),
    EMER("http://apollo-meta-server.emer.jimistore.com"),
    SANDBOX("http://apollo-meta-server-sandbox.jimistore.com"),
    PROD("http://apollo-meta-server.jimistore.com");

    /**
     * meta地址
     */
    String address;

    MetaAddressEnum(String address){
        this.address = address;
    };

    public String getAddress() {
        return address;
    }
}
