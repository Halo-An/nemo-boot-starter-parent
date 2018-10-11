package com.jimistore.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jimistore.boot.nemo.rpc.eureka.server.helper.DingDingCaller;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=NemoRpcEurekaServerApplication.class)
public class NemoRpcEurekaServerApplicationTests {
	
	@Autowired
	DingDingCaller caller;
	
	@Value("${alerm.dingding.to-robot:}")
	String toRobot;

	@Value("#{'${alerm.dingding.to-phone:18667163872}'.split(',')}")
	String[] toPhones;

	@Test
	public void contextLoads() {
	}

	@Test
	public void sendTestMsg() {
		caller.sendRobotNotice("hello", toRobot, toPhones);
	}
}
