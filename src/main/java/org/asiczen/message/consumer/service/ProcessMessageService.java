package org.asiczen.message.consumer.service;

import org.asiczen.message.consumer.model.OriginalMessage;
import org.springframework.stereotype.Service;

@Service
public interface ProcessMessageService {

	public void processMessage(OriginalMessage message);

	/*
	 * Step 1: Add Organisation Step 2: Add Vehicle Number Step 3: Add Vehice Type
	 * Step 4:
	 */
}
