package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.jimistore.boot.nemo.core.api.exception.ValidatedException;
import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;

public class ResponseExceptionHandle implements HandlerExceptionResolver, PriorityOrdered {

	private static Logger LOG = LoggerFactory.getLogger(ResponseExceptionHandle.class);

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		LOG.error(ex.getMessage(), ex);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");

		if (ex instanceof UndeclaredThrowableException) {
			ex = (Exception) ((UndeclaredThrowableException) ex).getUndeclaredThrowable();
		}

		if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException mcve = (ConstraintViolationException) ex;
			@SuppressWarnings("rawtypes")
			ConstraintViolation cv = mcve.getConstraintViolations().iterator().next();
			String[] fields = cv.getPropertyPath().toString().split("\\.");
			String field = fields[fields.length - 1];
			ex = new ValidatedException(new StringBuilder(cv.getMessage()).append(field).toString(), ex);
		}

		if (ex instanceof HttpMessageNotReadableException) {
			HttpMessageNotReadableException hmnre = (HttpMessageNotReadableException) ex;
			if (hmnre.getCause() instanceof InvalidFormatException) {
				InvalidFormatException ife = (InvalidFormatException) hmnre.getCause();
				List<?> list = ife.getPath();
				if (list != null && list.size() > 0) {
					Object obj = list.get(0);
					if (obj instanceof JsonMappingException.Reference) {
						JsonMappingException.Reference ref = (JsonMappingException.Reference) obj;

						ex = new ValidatedException(new StringBuilder(ref.getFieldName()).append(" must be ")
								.append(ife.getTargetType().getSimpleName())
								.toString(), ex);
					}
				}
			} else if (hmnre.getCause() instanceof JsonParseException || (hmnre.getCause().getCause() != null
					&& hmnre.getCause().getCause() instanceof JsonParseException)) {
				ex = new ValidatedException("request body must be json");
			}
		}

		try {
			Response<?> resp = Response.error(ex);
			response.getWriter().print(JsonString.toJson(resp));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ModelAndView();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
