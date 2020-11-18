package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

import com.jimistore.boot.nemo.dao.hibernate.config.HibernateProperties;

public class NemoNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {

	private static final long serialVersionUID = 1L;

	private boolean under;

	public void setUnder(boolean under) {
		this.under = under;
	}

	public void setHibernateProperties(HibernateProperties hibernateProperties) {
		under = hibernateProperties.getNameStrategyUnder();
	}

	@Override
	public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
		if (under) {
			return toIdentifier(this.addUnderscores(source.getEntityNaming().getJpaEntityName()),
					source.getBuildingContext());
		}
		return super.determinePrimaryTableName(source);
	}

	@Override
	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
		if (under) {
			return toIdentifier(this.addUnderscores(this.transformAttributePath(source.getAttributePath())),
					source.getBuildingContext());
		}
		return super.determineBasicColumnName(source);
	}

	public String classToTableName(String name) {
		if (under) {
			return this.addUnderscores(name);
		}
		return name;
	}

	public String propertyToColumnName(String name) {
		if (under) {
			return this.addUnderscores(name);
		}
		return name;
	}

	protected String addUnderscores(String name) {
		StringBuilder buf = new StringBuilder(name.replace('.', '_'));
		for (int i = 1; i < buf.length() - 1; i++) {
			if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i))
					&& Character.isLowerCase(buf.charAt(i + 1))) {
				buf.insert(i++, '_');
			}
		}
		return buf.toString().toLowerCase(Locale.ROOT);
	}
}