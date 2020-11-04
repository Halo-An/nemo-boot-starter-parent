package com.jimistore.boot.nemo.dao.hibernate.qbatis;

/**
 * 自定义spel函数适配器
 * 
 * @author chenqi
 * @date 2020年9月4日
 *
 */
public interface IDaoSpelFunc extends CharSequence {

	/**
	 * spel变量关键字
	 * 
	 * @return
	 */
	public String getKey();

	@Override
	default int length() {
		return this.toString().length();
	}

	@Override
	default char charAt(int index) {
		return this.toString().charAt(index);
	}

	@Override
	default CharSequence subSequence(int start, int end) {
		return this.toString().subSequence(start, end);
	}

}
