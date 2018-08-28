package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;

public interface IPublisherContainer {
	
	/**
	 * 获取所有的发布者
	 * @param method
	 * @param targetClass
	 * @return
	 */
	public Collection<Publisher> list();
	
	/**
	 * 创建一个publisher
	 * @param publisher
	 */
	public void create(Publisher publisher);
	
	/**
	 * 删除publisher
	 * @param publisher
	 */
	public void delete(String publisherKey);
	
	

}
