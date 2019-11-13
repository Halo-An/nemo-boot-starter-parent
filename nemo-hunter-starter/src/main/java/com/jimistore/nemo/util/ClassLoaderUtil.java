package com.jimistore.nemo.util;

/**
 * 类加载工具
 *
 * @author hexin
 */
public class ClassLoaderUtil {

    public static boolean isClassPresent(String className){
        try {
            Class.forName(className);
            return true;
        }catch (ClassNotFoundException e){
            return false;
        }
    }
}
