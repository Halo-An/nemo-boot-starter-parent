package com.jimistore.boot.nemo.core.response;

import java.io.PrintWriter;
import java.util.List;

import com.jimistore.boot.nemo.core.api.annotation.Json;
import com.jimistore.boot.nemo.core.api.annotation.JsonExclusion;
import com.jimistore.boot.nemo.core.api.annotation.JsonTitleAlias;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Json(notNull = true)
public class PageBean<T> extends Response {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String listUser; // 操作用户

	@JsonExclusion
	private String hql; // 数据查询hql条件

	@JsonExclusion
	private String id; // 唯一标识

	@JsonExclusion
	private String countHql; // 数据统计hql条件

	@JsonExclusion
	private String sql; // 数据查询sql条件

	@JsonExclusion
	private String countSql; // 数据统计sql条件

	@JsonExclusion
	private String totalSql; // 数据合计sql条件

	private Integer pageSize = 12; // 每页数据条数

	private Integer pageNum = 1; // 页码

	@SuppressWarnings("unused")
	private Integer pageCount; // 总页数

	private Integer dataSize; // 总数据条数

	@JsonExclusion
	private Boolean count; // 是否統計數據行

	@SuppressWarnings("unused")
	private Boolean next; // 是否有下一页

	private String query; // 查询值

	private String qtype; // 查询字段

	private String sortName; // 排序字段

	private String sortOrder; // 排序方式

	@JsonTitleAlias(alias = "rows")
	@JsonExclusion
	private List<T> dataList; // 数据集合

	@JsonExclusion
	private String[] fields; // 需要查询的字段
	@JsonExclusion
	private String[] defaultFields = new String[] { "code", "pageSize", "pageNum", "pageCount", "dataSize",
			"dataList" }; // 需要查询的字段

	public PageBean() {

	}

	public String toHql() {
		return hql;
	}

	public String toSql() {
		return sql;
	}

	public String toCountHql() {
		return countHql;
	}

	public String toCountSql() {
		return countSql;
	}

	public String toTotalSql() {
		return totalSql;
	}

	public PageBean setTotalSql(String totalSql) {
		this.totalSql = totalSql;
		return this;
	}

	public PageBean setHql(String str) {
		if (str == null) {
			return this;
		}
		StringBuilder hql = new StringBuilder(str);
		if (!(hql.indexOf(" where ") > 0 || hql.indexOf(" WHERE ") > 0)) {
			hql.append(" where 1=1");
		}
		if (qtype != null && qtype.trim().length() > 0) {
			hql.append(" and " + qtype + " like '%" + query + "%' ");
		}
		this.countHql = hql.toString();

		int index = countHql.toUpperCase().lastIndexOf("ORDER BY");
		if (index > 0) {
			countHql = countHql.substring(0, index);
		}
		if (sortName != null && sortName.trim().length() > 0
				&& (hql.toString().toUpperCase().indexOf("ORDER BY") < 0
						|| hql.toString().toUpperCase().indexOf("(ORDER BY") > 0
						|| hql.toString().toUpperCase().indexOf("( ORDER BY") > 0)) {
			hql.append(" order by " + sortName + " " + sortOrder);
		}
		this.hql = hql.toString();
		return this;
	}

	public PageBean setSql(String str) {
		if (str == null) {
			return this;
		}
		StringBuilder sql = new StringBuilder(str);
		if (!(sql.indexOf(" where ") > 0 || sql.indexOf(" WHERE ") > 0)) {
			sql.append(" where 1=1");
		}
		if (qtype != null && qtype.trim().length() > 0) {
			sql.append(" and " + qtype + " like '%" + query + "%' ");
		}
		countSql = sql.toString();
		int index = countSql.toUpperCase().lastIndexOf("ORDER BY");
		int index_ = countSql.toUpperCase().lastIndexOf(")");
		if (index > 0 && index_ < index) {
			countSql = countSql.substring(0, index);
		}
		if (sortName != null && sortName.trim().length() > 0
				&& (sql.toString().toUpperCase().indexOf("ORDER BY") < 0
						|| sql.toString().toUpperCase().indexOf("(ORDER BY") > 0
						|| sql.toString().toUpperCase().indexOf("( ORDER BY") > 0)) {
			sql.append("select * from (" + sql + ") t" + " order by " + sortName + " " + sortOrder);
		}
		this.sql = sql.toString();
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public PageBean setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public PageBean setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public Integer getDataSize() {
		return dataSize;
	}

	public PageBean setDataSize(Integer dataSize) {
		this.dataSize = dataSize;
		return this;
	}

	public Integer getPageCount() {
		if (dataSize == null || pageSize == null) {
			return null;
		}
		return dataSize % pageSize == 0 ? dataSize / pageSize : dataSize / pageSize + 1;
	}

	public String getQuery() {
		return query;
	}

	public PageBean setQuery(String query) {
		this.query = query;
		return this;
	}

	public String getQtype() {
		return qtype;
	}

	public PageBean setQtype(String qtype) {
		this.qtype = qtype;
		return this;
	}

	public String getSortName() {
		return sortName;
	}

	public PageBean setSortName(String sortName) {
		this.sortName = sortName;
		if (sortName != null && sortOrder == null) {
			sortOrder = "asc";
		}
		return this;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public PageBean setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public PageBean setDataList(List dataList) {
		this.dataList = dataList;
		super.setData(dataList);
		return this;
	}

	public String[] getOutFields() {
		if (fields == null || fields.length == 0) {
			return null;
		}
		String[] fieldNames = new String[defaultFields.length + (fields != null ? fields.length : 0)];
		for (int i = 0; i < defaultFields.length; i++) {
			fieldNames[i] = defaultFields[i];
		}
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				fieldNames[i + defaultFields.length] = "data." + fields[i];
			}
		}
		return fieldNames;
	}

	public String[] getFields() {
		return fields;
	}

	public PageBean setFields(String[] fields) {
		this.fields = fields;
		return this;
	}

	public String getListUser() {
		return listUser;
	}

	public PageBean setListUser(String listUser) {
		this.listUser = listUser;
		return this;
	}

	public String[] getDefaultFields() {
		return defaultFields;
	}

	public PageBean setDefaultFields(String[] defaultFields) {
		this.defaultFields = defaultFields;
		return this;
	}

	@Override
	public Object getData() {
		return dataList;
	}

	@Override
	public String getCode() {

		return "200";
	}

	public void print(PrintWriter writer) {
		writer.print(this);
	}

	public Boolean isCount() {
		return count;
	}

	public Boolean getCount() {
		return count;
	}

	public void setCount(Boolean count) {
		this.count = count;
	}

	public Boolean isNext() {
		return dataList != null && dataList.size() >= pageSize;
	}

	public Boolean getNext() {
		return dataList != null && dataList.size() >= pageSize;
	}

	public void setNext(Boolean next) {
		this.next = next;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
