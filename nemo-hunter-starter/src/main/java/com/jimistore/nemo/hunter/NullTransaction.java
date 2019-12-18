package com.jimistore.nemo.hunter;

import com.jimistore.nemo.spi.Transaction;

/**
 * 空实现
 * 未接入cat时不触发埋点
 *
 * @author hexin
 */
public class NullTransaction implements Transaction {

    @Override
    public void setStatus(String status) {

    }

    @Override
    public void setStatus(Throwable e) {

    }

    @Override
    public void addData(String key, Object value) {

    }

    @Override
    public void complete() {

    }
}
