package com.jimistore.boot.nemo.dao.hibernate.helper;

public class NotEmptySpelFunc implements ISpelExtendFunc {

	@Override
	public String getKey() {
		return "notEmpty";
	}

	@Override
	public Object format(String format, Object... value) {
		for(Object obj:value){
			if(obj==null){
				return "";
			}
			if(obj.toString().trim().length()==0){
				return "";
			}
		}
		return String.format(format, value);
	}

}
