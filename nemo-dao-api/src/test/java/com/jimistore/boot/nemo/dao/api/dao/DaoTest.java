package com.jimistore.boot.nemo.dao.api.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jimistore.boot.nemo.dao.api.enums.AndOr;
import com.jimistore.boot.nemo.dao.api.enums.Compare;
import com.jimistore.boot.nemo.dao.api.enums.OrderType;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.Order;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class DaoTest {
	
	private static final String COLUMN_USERNAME = "username";
	
	@Autowired
	IDao dao;

	@Test
	public void test() {
		String username=null;
		
		Filter filter = Filter
				.where(FilterEntry.create(COLUMN_USERNAME, Compare.eq, username))
				.and(AndOr.or, 
					FilterEntry.create(COLUMN_USERNAME, Compare.lt, username),
					FilterEntry.create(COLUMN_USERNAME, Compare.gt, username));
		
		Order order = Order.create(COLUMN_USERNAME, OrderType.asc);
		
		dao.list(Order.class, Query.create(filter, 0, 10, order));
	}

}
