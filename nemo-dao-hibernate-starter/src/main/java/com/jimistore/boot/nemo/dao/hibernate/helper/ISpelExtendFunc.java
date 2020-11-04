package com.jimistore.boot.nemo.dao.hibernate.helper;

import com.jimistore.boot.nemo.dao.hibernate.qbatis.IDaoSpelFunc;

/**
 * 自定义spel函数适配器
 * 
 * @author chenqi
 * @Date 2018年8月22日
 *
 */
public interface ISpelExtendFunc extends IDaoSpelFunc {

	/**
	 * 格式化函数
	 * 
	 * @param format
	 * @param value
	 * @return
	 */
	public Object format(String format, Object... value);

}
