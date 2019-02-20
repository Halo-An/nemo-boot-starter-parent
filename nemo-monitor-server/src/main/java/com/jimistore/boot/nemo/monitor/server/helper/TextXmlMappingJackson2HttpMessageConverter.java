package com.jimistore.boot.nemo.monitor.server.helper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TextXmlMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public TextXmlMappingJackson2HttpMessageConverter() {
		super();
		this.init();
	}

	public TextXmlMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper);
		this.init();
	}
	
	public void init(){
		List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        mediaTypes.add(new MediaType("text", "html", Charset.defaultCharset()));
        mediaTypes.add(new MediaType(MediaType.TEXT_XML, Charset.defaultCharset()));
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        setSupportedMediaTypes(mediaTypes);
	}
	
	

}
