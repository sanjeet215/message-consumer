package org.asiczen.message.consumer.service;

import org.asiczen.message.consumer.model.ConvertedMessage;
import org.springframework.stereotype.Service;

@Service
public interface VehicleInfoPublisher {

	void publish(ConvertedMessage message);
}
