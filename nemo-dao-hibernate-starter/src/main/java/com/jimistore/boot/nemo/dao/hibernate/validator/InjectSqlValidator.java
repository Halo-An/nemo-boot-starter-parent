package com.jimistore.boot.nemo.dao.hibernate.validator;

import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class InjectSqlValidator implements IInjectSqlValidator {
	
	/**
	 * 非法字符串
	 */
	public String[] errStr={
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

	@Override
	public void check(Query query) throws QueryValidatorException {
		if(query==null||query.getFilter()==null){
			return ;
		}
		Filter filter = query.getFilter();
		do{
			for(FilterEntry filterEntry : filter.getFilterEntrys()){
				//是否字符串
				if(filterEntry.getValue()!=null && filterEntry.getValue() instanceof String){
					for(String str:errStr){
						if(filterEntry.getValue().toString().indexOf(str)>=0){
							throw new QueryValidatorException();
						}
					}
				}
			}
			
		}while((filter=filter.getNext())!=null);
	}

}
