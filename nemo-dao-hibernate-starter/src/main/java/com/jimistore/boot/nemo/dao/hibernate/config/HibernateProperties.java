package com.jimistore.boot.nemo.dao.hibernate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hibernate")
public class HibernateProperties {

	public static class Hbm2ddl {

		String auto = "update";

		public String getAuto() {
			return auto;
		}

		public void setAuto(String auto) {
			this.auto = auto;
		}

	}

	public static class Query {

		String plan_cache_max_size = "128";

		String plan_parameter_metadata_max_size = "128";

		public String getPlan_cache_max_size() {
			return plan_cache_max_size;
		}

		public Query setPlan_cache_max_size(String plan_cache_max_size) {
			this.plan_cache_max_size = plan_cache_max_size;
			return this;
		}

		public String getPlan_parameter_metadata_max_size() {
			return plan_parameter_metadata_max_size;
		}

		public Query setPlan_parameter_metadata_max_size(String plan_parameter_metadata_max_size) {
			this.plan_parameter_metadata_max_size = plan_parameter_metadata_max_size;
			return this;
		}

	}

	String show_sql = "false";

	String packagesToScan = "*";

	String dialect = "";

	Boolean nameStrategyUnder = false;

	Hbm2ddl hbm2ddl = new Hbm2ddl();

	Query query = new Query();

	public String getShow_sql() {
		return show_sql;
	}

	public void setShow_sql(String show_sql) {
		this.show_sql = show_sql;
	}

	public String getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public Hbm2ddl getHbm2ddl() {
		return hbm2ddl;
	}

	public void setHbm2ddl(Hbm2ddl hbm2ddl) {
		this.hbm2ddl = hbm2ddl;
	}

	public Boolean getNameStrategyUnder() {
		return nameStrategyUnder;
	}

	public void setNameStrategyUnder(Boolean nameStrategyUnder) {
		this.nameStrategyUnder = nameStrategyUnder;
	}

	public Query getQuery() {
		return query;
	}

	public HibernateProperties setQuery(Query query) {
		this.query = query;
		return this;
	}

}
