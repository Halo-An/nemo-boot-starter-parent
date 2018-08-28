package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PublisherContainer implements IPublisherContainer {
	
	protected Map<String, Publisher> publisherMap = new HashMap<String, Publisher>();

	@Override
	public Collection<Publisher> list() {
		return publisherMap.values();
	}

	@Override
	public void create(Publisher publisher) {
		if(publisherMap.containsKey(publisher.getKey())){
			throw new RuntimeException(String.format("publisher[%s] exist", publisher.getKey()));
		}
		publisherMap.put(publisher.getKey(), publisher);
	}

	@Override
	public void delete(String publisher) {
		if(!publisherMap.containsKey(publisher)){
			throw new RuntimeException(String.format("publisher[%s] not exist", publisher));
		}
		publisherMap.remove(publisher);

	}

}
