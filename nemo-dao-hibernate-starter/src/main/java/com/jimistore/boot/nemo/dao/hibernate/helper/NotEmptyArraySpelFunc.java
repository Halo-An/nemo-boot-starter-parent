package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.springframework.stereotype.Component;

@Component
public class NotEmptyArraySpelFunc implements ISpelExtendFunc {

	@Override
	public String getKey() {
		return "notEmptyArray";
	}

	@Override
	public Object format(String format, Object... value) {
		Object[] objs = value;
		if (objs == null || objs.length == 0) {
			return "";
		}
		Object[] params = (Object[]) objs[0];
		if (params == null || params.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Object obj : params) {
			if (obj instanceof Number) {
				sb.append(obj);
			} else {
				sb.append("'").append(obj).append("'");
			}
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return String.format(format, sb.toString());
	}

}
