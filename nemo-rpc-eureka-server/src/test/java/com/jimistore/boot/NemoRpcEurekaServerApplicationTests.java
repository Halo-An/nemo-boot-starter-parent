package com.jimistore.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.jimistore.boot.nemo.rpc.eureka.server.helper.DingDingCaller;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingNoticeRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NemoRpcEurekaServerApplication.class)
public class NemoRpcEurekaServerApplicationTests {

	@Autowired
	DingDingCaller caller;

	@Test
	public void contextLoads() {
	}

	@Test
	public void sendTestMsg() {
		try {
			caller.notice(new DingDingNoticeRequest().setTitle("测试报警")
					.setRobot(
							"https://oapi.dingtalk.com/robot/send?access_token=4b6aa2c7589bc30cffe313bebd7dcfe4303aa83e429317bbd20651206c9d4e7b")
					.setTos(new String[] { "18667163872" })
					.setContent(NemoRpcEurekaServerApplicationTests.readFileContent("monitor-msg.md")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String readFileContent(String fileName) throws IOException {
		ClassPathResource classPathResource = new ClassPathResource(fileName);
		File file = classPathResource.getFile();
		BufferedReader reader = null;
		StringBuffer sbf = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				sbf.append(tempStr).append("\n");
			}
			reader.close();
			return sbf.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return sbf.toString();
	}
}
