package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.jimistore.boot.nemo.dao.hibernate.annotation.Param;

public class DaoExecutorHelper {

	private final SpelExpressionParser spel = new SpelExpressionParser();
	private final TemplateParserContext tpc = new TemplateParserContext("${", "}");

	/**
	 * 初始化spel上下文
	 * 
	 * @param method
	 * @param params
	 * @return
	 */
	public StandardEvaluationContext initContext(Method method, List<IDaoSpelFunc> spelFuncList, Object[] params) {

		StandardEvaluationContext context = new StandardEvaluationContext();
		if (spelFuncList != null) {
			for (IDaoSpelFunc func : spelFuncList) {
				context.setVariable(func.getKey(), func);
			}
		}
		Parameter[] paramTypes = method.getParameters();
		for (int i = 0; i < params.length; i++) {
			context.setVariable(String.format("%s%s", "p", i), params[i]);
			Param pt = paramTypes[i].getAnnotation(Param.class);
			if (pt != null) {
				context.setVariable(pt.value(), params[i]);
			}
		}
		if (params != null && params.length > 0) {
			context.setRootObject(params[0]);
		}
		return context;
	}

	/**
	 * 解析spel
	 * 
	 * @param context
	 * @param exp
	 * @param type
	 * @return
	 */
	public <T> T parseExpression(StandardEvaluationContext context, String exp, Class<T> type) {
		return spel.parseExpression(exp, tpc).getValue(context, type);
	}

}
