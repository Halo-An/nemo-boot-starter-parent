package com.jimistore.boot.nemo.fuse.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.fuse.config.FuseProperties;
import com.jimistore.boot.nemo.fuse.enums.FuseState;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;
import com.jimistore.boot.nemo.sliding.window.core.ILogicSubscriber;
import com.jimistore.boot.nemo.sliding.window.core.INotice;
import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.NoticeLogicEvent;
import com.jimistore.boot.nemo.sliding.window.core.PublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

/**
 * 默认策略（滑动窗口实现）
 * 
 * @author chenqi
 * @date 2019年1月30日
 *
 */
public class DefaultFuseStrategy implements IFuseStrategy {

	private static final Logger log = Logger.getLogger(DefaultFuseStrategy.class);

	protected FuseProperties fuseProperties;

	protected SlidingWindowTemplate slidingWindowTemplate;

	public static final String REQUEST_KEY_FORMAT = "fuse-%s-request";

	public static final String REQUEST_EXCEPTION_KEY_FORMAT = "fuse-%s-exception";

	public static final String REQUEST_MATCH_KEY_FORMAT = "fuse-%s-";

	public DefaultFuseStrategy setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}

	public DefaultFuseStrategy setFuseProperties(FuseProperties fuseProperties) {
		this.fuseProperties = fuseProperties;
		return this;
	}

	@Override
	public void executeBefore(IFuseInfo fuseInfo) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("request execute before, the key is %s", fuseInfo.getKey()));
		}
		slidingWindowTemplate.publish(new PublishEvent<Integer>().setTime(System.currentTimeMillis())
				.setTopicKey(String.format(REQUEST_KEY_FORMAT, fuseInfo.getKey())).setValue(1));
	}

	@Override
	public void executeSuccess(IFuseInfo fuseInfo) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("request execute success, the key is %s", fuseInfo.getKey()));
		}
		FuseState state = fuseInfo.getFuseState();
		if (state.equals(FuseState.TRYING)) {
			if (fuseInfo instanceof FuseInfo) {
				log.debug(String.format("fuse state changing, the key is %s, %s==>%s", fuseInfo.getKey(),
						fuseInfo.getFuseState(), FuseState.CONNECT));
				((FuseInfo) fuseInfo).setFuseState(FuseState.CONNECT);
			}
		}
	}

	@Override
	public void executeException(IFuseInfo fuseInfo, Throwable throwable) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("request execute exception, the key is %s", fuseInfo.getKey()));
		}

		if (throwable instanceof TaskInternalException || throwable instanceof TimeOutException) {
			FuseState state = fuseInfo.getFuseState();
			if (state.equals(FuseState.TRYING)) {
				if (fuseInfo instanceof FuseInfo) {
					log.debug(String.format("fuse state changing, the key is %s, %s==>%s", fuseInfo.getKey(),
							fuseInfo.getFuseState(), FuseState.OPEN));
					((FuseInfo) fuseInfo).setFuseState(FuseState.OPEN);
				}
			}

			slidingWindowTemplate.publish(new PublishEvent<Integer>().setTime(System.currentTimeMillis())
					.setTopicKey(String.format(REQUEST_EXCEPTION_KEY_FORMAT, fuseInfo.getKey())).setValue(1));

		}
	}

	/**
	 * 当调用信息变更时
	 * 
	 * @param fuse
	 * @param event
	 */
	protected void changing(IFuseInfo fuseInfo, INoticeEvent<?> event) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("request changing, the key is %s", fuseInfo.getKey()));
		}
		FuseState state = fuseInfo.getFuseState();
		if (state.equals(FuseState.CONNECT)) {
			Double value = event.getValue().get(0).doubleValue();
			NoticeLogicEvent<?> logicEvent = (NoticeLogicEvent<?>) event;
			String requestKey = String.format(REQUEST_KEY_FORMAT, fuseInfo.getKey());
			if (logicEvent.getOriginMap() == null || logicEvent.getOriginMap().size() == 0) {
				return;
			}
			int openCount = logicEvent.getOriginMap().get(0).get(requestKey).intValue();
			// 如果调用的异常率大于50%且大于等于调用量，则断开
			if (value != null && !value.isNaN() && value >= fuseProperties.getOpenRatioThreshold()
					&& openCount >= fuseProperties.getOpenCountThreshold()) {
				if (fuseInfo instanceof FuseInfo) {
					log.debug(String.format("fuse state changing, the key is %s, %s==>%s", fuseInfo.getKey(),
							fuseInfo.getFuseState(), FuseState.OPEN));
					((FuseInfo) fuseInfo).setFuseState(FuseState.OPEN);
				}
			}
		} else if (state.equals(FuseState.OPEN)) {
			Double value = event.getValue().get(0).doubleValue();
			// 如果调用量已经停滞了，每分钟去尝试一次
			if (value == null || value.isNaN() || value <= fuseProperties.getTryRatioThreshold()) {
				log.debug(String.format("fuse state changing, the key is %s, %s==>%s", fuseInfo.getKey(),
						fuseInfo.getFuseState(), FuseState.TRY));
				((FuseInfo) fuseInfo).setFuseState(FuseState.TRY);
			}
		}
	}

	@Override
	public void creating(IFuseInfo fuseInfo) {
		if (fuseInfo instanceof FuseInfo) {
			((FuseInfo) fuseInfo).setFuseState(FuseState.CONNECT);
		}

		String requestKey = String.format(REQUEST_KEY_FORMAT, fuseInfo.getKey());
		String exceptionKey = String.format(REQUEST_EXCEPTION_KEY_FORMAT, fuseInfo.getKey());
		String matchKey = "#b/#a";
		Map<String, String> variableMap = new HashMap<String, String>();
		variableMap.put(requestKey, "a");
		variableMap.put(exceptionKey, "b");

		// 创建调用的计数器
		slidingWindowTemplate.createCounter(new Topic().setKey(requestKey).setTimeUnit(TimeUnit.SECONDS)
				.setCapacity(3600).setValueType(Integer.class));

		// 创建调用异常的计数器
		slidingWindowTemplate.createCounter(new Topic().setKey(exceptionKey).setTimeUnit(TimeUnit.SECONDS)
				.setCapacity(3600).setValueType(Integer.class));

		// 订阅调用监控信息变更
		slidingWindowTemplate.subscribe(new ILogicSubscriber() {

			@Override
			public INotice getNotice() {
				return new INotice() {

					@Override
					public void notice(INoticeEvent<?> event) {
						changing(fuseInfo, event);
					}

				};
			}

			@Override
			public String getTopicMatch() {
				return matchKey;
			}

			@Override
			public Integer getLength() {
				return 1;
			}

			@Override
			public Class<?> getValueType() {
				return Double.class;
			}

			@Override
			public Long getInterval() {
				return fuseProperties.getCheckInterval();
			}

			@Override
			public TimeUnit getTimeUnit() {
				return TimeUnit.MINUTES;
			}

			@Override
			public String getCondition() {
				return "true";
			}

			@Override
			public Map<String, String> getTopicVariableMap() {
				return variableMap;
			}
		});
	}

}
