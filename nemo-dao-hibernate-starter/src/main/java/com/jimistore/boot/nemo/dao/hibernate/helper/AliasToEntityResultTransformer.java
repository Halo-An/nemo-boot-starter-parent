package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import com.jimistore.boot.nemo.core.util.ClassUtil;

public class AliasToEntityResultTransformer<T> extends AliasedTupleSubsetResultTransformer {

	private static final Logger log = Logger.getLogger(AliasToEntityResultTransformer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Class<T> entityClass;

	private AliasToEntityResultTransformer<T> setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	public static final <E> AliasToEntityResultTransformer<E> create(Class<E> entityClass) {
		return new AliasToEntityResultTransformer<E>().setEntityClass(entityClass);
	}

	@Override
	public T transformTuple(Object[] tuple, String[] aliases) {

		try {
			T target = entityClass.newInstance();
			ImprovedNamingStrategy strategy = MutilHibernateNamingStrategy.getHibernateNamingStrategy();
			List<Field> fieldList = ClassUtil.getFields(entityClass);
			for (Field field : fieldList) {
				String fieldName = field.getName();
				String columnName = strategy.propertyToColumnName(fieldName);
				for (int i = 0; i < aliases.length; i++) {
					if (columnName.equals(aliases[i]) || fieldName.equals(aliases[i])) {
						try {
							this.fillValue(target, field, tuple[i]);
						} catch (Exception e) {
							log.warn(String.format("fill value error:%s[%s]", columnName, aliases[i]), e);
						}
					}
				}
			}
			return target;
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		// TODO Auto-generated method stub
		return false;
	}

	private void fillValue(Object target, Field field, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (value != null) {
			if (field.getType().equals(Long.class)) {
				value = Long.parseLong(value.toString());
			} else if (field.getType().equals(Short.class)) {
				value = Short.parseShort(value.toString());
			}
		}

		String fieldName = field.getName();
		String methodName = String.format("set%s%s", fieldName.substring(0, 1).toUpperCase(), fieldName.substring(1));
		if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) value;
			if (Long.class.isAssignableFrom(field.getType()) || long.class.isAssignableFrom(field.getType())) {
				value = bd.longValue();
			} else if (Integer.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType())) {
				value = bd.intValue();
			} else if (Short.class.isAssignableFrom(field.getType()) || short.class.isAssignableFrom(field.getType())) {
				value = bd.shortValue();
			} else if (Byte.class.isAssignableFrom(field.getType()) || byte.class.isAssignableFrom(field.getType())) {
				value = bd.byteValue();
			} else if (Double.class.isAssignableFrom(field.getType())
					|| double.class.isAssignableFrom(field.getType())) {
				value = bd.doubleValue();
			} else if (Float.class.isAssignableFrom(field.getType()) || float.class.isAssignableFrom(field.getType())) {
				value = bd.floatValue();
			}
		}
		entityClass.getMethod(methodName, field.getType()).invoke(target, value);
	}

}
