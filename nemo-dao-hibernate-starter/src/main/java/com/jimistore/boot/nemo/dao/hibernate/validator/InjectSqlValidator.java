package com.jimistore.boot.nemo.dao.hibernate.validator;

import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class InjectSqlValidator implements IInjectSqlValidator {
	
	/**
	 * 非法字符串
	 */
	public static final String[] errStr={
			"'",
			" ",
			",",
			";",
			"*",
			"/",
			"\\",
			"+",
			"-",
			"=",
			"!",
			"<",
			">",
			"|",
			"?",
			"&",
			"$",
			"^",
			"@",
			"#"
			};
	
	public static void check(Object obj) throws QueryValidatorException{
		//是否字符串
		if(obj!=null && obj instanceof String){
			for(String str:errStr){
				if(obj.toString().indexOf(str)>=0){
					throw new QueryValidatorException();
				}
			}
		}
	}

	@Override
	public void check(Query query) throws QueryValidatorException {
		if(query==null||query.getFilter()==null){
			return ;
		}
		Filter filter = query.getFilter();
		do{
			for(FilterEntry filterEntry : filter.getFilterEntrys()){
				check(filterEntry.getValue());
			}
			
		}while((filter=filter.getNext())!=null);
	}

}
