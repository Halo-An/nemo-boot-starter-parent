package com.jimistore.boot.nemo.dao.hibernate.request;

import java.util.ArrayList;
import java.util.List;

import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.Target;
import com.jimistore.boot.nemo.dao.hibernate.enums.JoinType;

public class SqlTarget extends Target {
	
	List<SqlTarget> joinList = new ArrayList<SqlTarget>();
	
	JoinType joinType;
	
	SqlTarget preTarget;
	
	String preKey;
	
	String selfKey;
	
	public SqlTarget(){}
	
	public static final SqlTarget create(Class<?> entityClass, Filter filter, String... outFieldNames){
		return new SqlTarget().setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames);
	}
	
	public static final SqlTarget createJoin(JoinType joinType, String preKey, String selfKey, Class<?> entityClass, Filter filter, String... outFieldNames){
		return new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(joinType);
	}
	
	public SqlTarget innerJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, String... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.inner));
		return this;
	}
	
	public SqlTarget leftJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, String... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.left));
		return this;
	}
	
	public SqlTarget rightJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, String... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.right));
		return this;
	}
	
	public SqlTarget fullJoin(String preKey, String selfKey, Class<?> entityClass, Filter filter, String... outFieldNames){
		joinList.add(new SqlTarget().setPreKey(preKey).setSelfKey(selfKey).setEntityClass(entityClass).setFilter(filter).setOutFieldNames(outFieldNames).setJoinType(JoinType.full));
		return this;
	}
	
	public SqlTarget join(SqlTarget sqlTarget){
		joinList.add(sqlTarget);
		return this;
		
	}

	public List<SqlTarget> getJoinList() {
		return joinList;
	}

	public SqlTarget setJoinList(List<SqlTarget> joinList) {
		this.joinList = joinList;
		return this;
	}

	public String getPreKey() {
		return preKey;
	}

	public SqlTarget setPreKey(String preKey) {
		this.preKey = preKey;
		return this;
	}

	public String getSelfKey() {
		return selfKey;
	}

	public SqlTarget setSelfKey(String selfKey) {
		this.selfKey = selfKey;
		return this;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public SqlTarget setJoinType(JoinType joinType) {
		this.joinType = joinType;
		return this;
	}

	@Override
	public SqlTarget setEntityClass(Class<?> entityClass) {
		super.setEntityClass(entityClass);
		return this;
	}

	@Override
	public SqlTarget setFilter(Filter filter) {
		super.setFilter(filter);
		return this;
	}

	public SqlTarget getPreTarget() {
		return preTarget;
	}

	public SqlTarget setPreTarget(SqlTarget preTarget) {
		this.preTarget = preTarget;
		return this;
	}

	@Override
	public SqlTarget setOutFieldNames(String[] fieldNames) {
		super.setOutFieldNames(fieldNames);
		return this;
	}

}
