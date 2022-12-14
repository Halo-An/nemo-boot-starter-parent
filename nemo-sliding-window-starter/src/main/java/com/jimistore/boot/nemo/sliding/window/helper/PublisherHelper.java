package com.jimistore.boot.nemo.sliding.window.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimistore.boot.nemo.core.api.exception.ValidatedException;
import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.sliding.window.annotation.Publish;
import com.jimistore.boot.nemo.sliding.window.core.IPublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.Publisher;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

public class PublisherHelper {

	private static final Logger LOG = LoggerFactory.getLogger(PublisherHelper.class);

	SlidingWindowTemplate slidingWindowTemplate;

	String service;

	public PublisherHelper setService(String service) {
		this.service = service;
		return this;
	}

	public String getService() {
		return service;
	}

	public PublisherHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}

	public void createPublisher(Method method) {
		Publish publish = AnnotationUtil.getAnnotation(method, Publish.class);
		slidingWindowTemplate.createPublisher(new Publisher().setAlias(publish.alias())
				.setKey(PublisherUtil.getPublisherKeyByMethod(method))
				.setService(service));
	}

	public void createCounter(Topic topic) {
		try {
			LOG.debug(String.format("create counter[%s] when it do not exist", topic.getKey()));
			slidingWindowTemplate.createCounter(topic);
		} catch (ValidatedException e) {
			LOG.warn(e.getMessage(), e);
		}

	}

	public void publish(IPublishEvent<?> event) {
		slidingWindowTemplate.publish(event);
	}

	public List<Topic> listTopicByPublisher(String publisherKey) {
		List<Topic> list = new ArrayList<Topic>();
		Collection<Topic> topicList = slidingWindowTemplate.listTopic();

		for (Topic topic : topicList) {
			if (topic.getPublisherKey().trim().length() > 0 && topic.getPublisherKey().equals(publisherKey)) {
				list.add(topic);
			}
		}
		return list;
	}

}
