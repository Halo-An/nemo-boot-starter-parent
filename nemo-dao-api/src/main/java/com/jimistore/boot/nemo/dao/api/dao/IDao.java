package com.jimistore.boot.nemo.dao.api.dao;

import java.util.List;

import com.jimistore.boot.nemo.dao.api.request.Query;

public interface IDao {
	
	/**
	 * 创建一个对象
	 * @param entity
	 * @return
	 */
	public Object create(Object entity);
	
	/**
	 * 删除一个对象
	 * @param entityClass
	 * @param query
	 * @return
	 */
	public <T> List<T> delete(Class<T> entityClass, Query query);
	
//	/**
//	 * 更新对象的部分数据
//	 * @param entityClass 实体类型
//	 * @param filter 过滤条件
//	 * @param entity 被更新的对象的键值对
//	 * @return
//	 */
//	public <T> List<T> update(Class<T> entityClass, Filter filter, Map<String,Object> entity);
	
	/**
	 * 更新对象
	 * @param entity 新的数据对象
	 * @return
	 */
	public Object update(Object entity);
	
	/**
	 * 批量查询
	 * @param entityClass 输出实体类型
	 * @param filter 过滤条件
	 * @param target
	 * @return
	 */
	public <T> List<T> list(Class<T> entityClass, Query query);
	
	/**
	 * 查询单个
	 * @param entityClass
	 * @param filter
	 * @param target
	 * @return
	 */
	public <T> T get(Class<T> entityClass, Query query);

}
