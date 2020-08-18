package org.asiczen.message.consumer.serviceimpl;

import org.asiczen.message.consumer.model.ConvertedMessage;
import org.asiczen.message.consumer.service.VehicleInfoPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisVehicleInfoPublisher implements VehicleInfoPublisher {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private ChannelTopic topic;

	@Override
	public void publish(ConvertedMessage message) {
		try {
			log.trace("publising data to Redis topic....");
			redisTemplate.convertAndSend(topic.getTopic(), message);
			log.trace("Data published to Redis topic successfully....");
		} catch (Exception ep) {
			log.error("Error while publishing the messages to server");
			log.error(ep.getLocalizedMessage());
		}

	}

}
