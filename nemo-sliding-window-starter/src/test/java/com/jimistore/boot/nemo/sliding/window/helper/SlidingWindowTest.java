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
import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.ISubscriber;
import com.jimistore.boot.nemo.sliding.window.core.PublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NemoSlidingWindowStarterApplication.class)
public class SlidingWindowTest {
	
	private static final Logger log = Logger.getLogger(SlidingWindowTest.class);

	@Test
	public void test() {
		String topic = "heabert";
		SlidingWindowTemplate sw = SlidingWindowTemplate
				.create(new SlidingWindowProperties().setCacheModel(SlidingWindowProperties.CACHE_MODEL_REDIS));
		sw.createCounter(topic, TimeUnit.SECONDS, 3600, Integer.class);
		sw.subscribe(new ISubscriber(){

			@Override
			public void notice(INoticeEvent<?> event) {
				log.info(String.format("notice %s:%s", event.getTopicKey(), event.getValue()));
			}

			@Override
			public String getTopicMatch() {
				return "*-order-nums";
			}

			@Override
			public Integer getLength() {
				return 10;
			}
			
		});
		
		
		for(int i=0;i<10000;i++){
			try {
				Integer value = 1+new Random().nextInt(9);
				sw.publish(new PublishEvent<Integer>().setTime(System.currentTimeMillis()).setTopicKey(topic).setValue(value));
				log.info(String.format("publish %s[%s]", topic, value));
				if(i%10==0){
					List<List<Integer>> dataList = sw.listWindow(topic, TimeUnit.SECONDS, 3600, Integer.class);
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
