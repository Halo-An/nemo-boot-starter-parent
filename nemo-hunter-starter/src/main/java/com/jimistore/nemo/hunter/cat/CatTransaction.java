package com.jimistore.nemo.hunter.cat;


import com.jimistore.nemo.spi.Transaction;

import java.lang.reflect.Method;

/**
 * cat自定义Transaction
 *
 * @author hexin
 */
public class CatTransaction implements Transaction {

    private static Class CAT_TRANSACTION_CLASS;
    private static Method SET_STATUS_METHOD;
    private static Method SET_STATUS_WITH_THROWABLE_METHOD;
    private static Method ADD_DATA_WITH_KEYANDVALUE_METHOD;
    private static Method COMPLETE_METHOD;

    private Object catTransaction;

    static {
        try {
            CAT_TRANSACTION_CLASS = Class.forName(CatNames.CAT_TRANSACTION_CLASS);
            SET_STATUS_METHOD = CAT_TRANSACTION_CLASS.getMethod(CatNames.SET_STATUS_METHOD, String.class);
            SET_STATUS_WITH_THROWABLE_METHOD = CAT_TRANSACTION_CLASS.getMethod(CatNames.SET_STATUS_METHOD, Throwable.class);
            ADD_DATA_WITH_KEYANDVALUE_METHOD = CAT_TRANSACTION_CLASS.getMethod(CatNames.ADD_DATA_METHOD, String.class, Object.class);
            COMPLETE_METHOD = CAT_TRANSACTION_CLASS.getMethod(CatNames.COMPLETE_METHOD);
        }catch (Throwable e){
            throw new IllegalStateException("init catTransaction fail", e);
        }
    }

    static void init(){
        //do nothing
    }

    public CatTransaction(Object catTransaction){
        this.catTransaction = catTransaction;
    }

    @Override
    public void setStatus(String status) {
        try {
            SET_STATUS_METHOD.invoke(catTransaction, status);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setStatus(Throwable e) {
        try {
            SET_STATUS_WITH_THROWABLE_METHOD.invoke(catTransaction, e);
        }catch (Throwable ex){
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void addData(String key, Object value) {
        try {
            ADD_DATA_WITH_KEYANDVALUE_METHOD.invoke(catTransaction, key, value);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void complete() {
        try {
            COMPLETE_METHOD.invoke(catTransaction);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }
}
