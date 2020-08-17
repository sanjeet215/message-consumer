package org.asiczen.message.consumer.service;

import org.asiczen.message.consumer.model.OriginalMessage;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageListener {

	@StreamListener(target = Sink.INPUT)
	public void handle(OriginalMessage message) {
		System.out.println(message.toString());
	}
}
