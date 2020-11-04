package com.jimistore.boot.nemo.dao.hibernate.qbatis;

public class NotEmptySpelFunc implements IDaoSpelFunc {

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public String format(String str, Object value) {
		return str;
	}

}
