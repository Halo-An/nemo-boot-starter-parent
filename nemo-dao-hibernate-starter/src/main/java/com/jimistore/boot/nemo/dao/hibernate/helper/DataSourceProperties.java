package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datasource")
public class DataSourceProperties {
	
	private String driverClass;
	
	private String jdbcUrl;
	
	private String user="root";
	
	private String password="root";

	private Integer minPoolSize=5;
	
	private Integer maxPoolSize=100;
	
	private Integer maxIdleTime=300;
	
	private Integer acquireIncrement=2;
	
	private Integer maxStatements=0;
	
	private Integer initialPoolSize=5;
	
	private Integer idleConnectionTestPeriod=300;
	
	private Integer acquireRetryAttempts=30;
	
	private Boolean breakAfterAcquireFailure=false;
	
	private Boolean testConnectionOnCheckout=false;
	
	private String characterEncoding="UTF-8";

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(Integer minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public Integer getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public Integer getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(Integer maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public Integer getAcquireIncrement() {
		return acquireIncrement;
	}

	public void setAcquireIncrement(Integer acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}

	public Integer getMaxStatements() {
		return maxStatements;
	}

	public void setMaxStatements(Integer maxStatements) {
		this.maxStatements = maxStatements;
	}

	public Integer getInitialPoolSize() {
		return initialPoolSize;
	}

	public void setInitialPoolSize(Integer initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public Integer getIdleConnectionTestPeriod() {
		return idleConnectionTestPeriod;
	}

	public void setIdleConnectionTestPeriod(Integer idleConnectionTestPeriod) {
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}

	public Integer getAcquireRetryAttempts() {
		return acquireRetryAttempts;
	}

	public void setAcquireRetryAttempts(Integer acquireRetryAttempts) {
		this.acquireRetryAttempts = acquireRetryAttempts;
	}

	public Boolean getBreakAfterAcquireFailure() {
		return breakAfterAcquireFailure;
	}

	public void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
		this.breakAfterAcquireFailure = breakAfterAcquireFailure;
	}

	public Boolean getTestConnectionOnCheckout() {
		return testConnectionOnCheckout;
	}

	public void setTestConnectionOnCheckout(Boolean testConnectionOnCheckout) {
		this.testConnectionOnCheckout = testConnectionOnCheckout;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	
	
}
