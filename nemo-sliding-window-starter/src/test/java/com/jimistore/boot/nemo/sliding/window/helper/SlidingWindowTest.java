package com.jimistore.boot.nemo.sliding.window.helper;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jimistore.boot.NemoSlidingWindowStarterApplication;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.INotice;
import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.IWarnSubscriber;
import com.jimistore.boot.nemo.sliding.window.core.PublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NemoSlidingWindowStarterApplication.class)
public class SlidingWindowTest {
	
	private static final Logger log = Logger.getLogger(SlidingWindowTest.class);

	@Test
	public void test() {
		String topicKey = "heabert";
		SlidingWindowTemplate sw = SlidingWindowTemplate
				.create(new SlidingWindowProperties().setCacheModel(SlidingWindowProperties.CACHE_MODEL_LOCAL));
		sw.createCounter(new Topic()
				.setKey(topicKey)
				.setTimeUnit(TimeUnit.SECONDS)
				.setCapacity(3600)
				.setValueType(Integer.class));
		sw.subscribe(new IWarnSubscriber(){

			@Override
			public String getTopicMatch() {
				return "*";
			}

			@Override
			public Integer getLength() {
				return 1440;
			}

			@Override
			public INotice getNotice() {
				return new INotice(){

					@Override
					public void notice(INoticeEvent<?> event) {
						log.info(String.format("notice %s:%s", event.getTopicKey(), event.getValue()));
					}
				};
			}

			@Override
			public String getCondition() {
				return "#cur>10";
			}
			
			
		});
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i=0;i<10000;i++){
			try {
				Integer value = 1+new Random().nextInt(9);
				sw.publish(new PublishEvent<Integer>().setTime(System.currentTimeMillis()).setTopicKey(topicKey).setValue(value));
				log.info(String.format("publish %s[%s]", topicKey, value));
				if(i%10==0){
					List<Integer> dataList = sw.window(topicKey, TimeUnit.SECONDS, 3600, Integer.class, System.currentTimeMillis());
					log.info(String.format("window list:%s", dataList));
				}
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fail("Not yet implemented");
	}

}
