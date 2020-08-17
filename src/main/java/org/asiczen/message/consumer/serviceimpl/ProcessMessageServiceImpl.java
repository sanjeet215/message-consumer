package org.asiczen.message.consumer.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.asiczen.message.consumer.model.ConvertedMessage;
import org.asiczen.message.consumer.model.OriginalMessage;
import org.asiczen.message.consumer.response.ServiceResponse;
import org.asiczen.message.consumer.service.ProcessMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessMessageServiceImpl implements ProcessMessageService {

	@Value("${BASE.URL}")
	private String BASE_URL;

	@Autowired
	SimpMessagingTemplate messageTemplate;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void processMessage(OriginalMessage message) {

		ConvertedMessage conmessage = new ConvertedMessage();
		try {

			log.trace("Imei number received from device: {}", message.getImei());
			conmessage.setImeiNumber(message.getImei());

			log.trace("Longitude Received from device: {}", message.getLng());
			conmessage.setLongitude(message.getLng());

			log.trace("Latitude Received from device: {}", message.getLat());
			conmessage.setLatitude(message.getLat());

			log.trace("Setting it true. This will always true as this the fresh event received from MB");
			conmessage.setCurrent(true);

			log.trace("Converting the date and time to string for displating on screen.");
			try {

				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String strDate = dateFormat.format(message.getTimestamp());
				conmessage.setDateTimestamp(strDate);
			} catch (Exception ep) {
				log.error("Error while converting the date");
				log.error(ep.getLocalizedMessage());
			}

		} catch (Exception ep) {

			log.warn("Error while persisting the message");
			log.warn(ep.getLocalizedMessage());
			log.warn(message.toString());
			log.error("Error while persisting the message. {}", ep.getMessage());
		}

	}

	/* This method is yet to get developed */
	private ServiceResponse getVehicleInfoFromRedis(String imei) {
		return null;
	}

	/*
	 * Method is to get the vehicle information from database i.e by calling the web
	 * service defined in application.properties file Finally add the map into Redis
	 */
	private ServiceResponse getVehicleInformation(String imei) {

		log.trace("Getting vehicle information by calling the rest web service..");
		ServiceResponse retresponse = new ServiceResponse();

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("imei", imei);

		log.trace("Queury partamter to web service is IMEI number {} ", imei);

		try {
			log.trace("invoking rest template to get the data for IMEI");
			ResponseEntity<ServiceResponse> response = restTemplate.getForEntity(BASE_URL + "?imei=" + imei,
					ServiceResponse.class, queryParams);

			if (response.getStatusCodeValue() == 200) {

				if (response.getBody().getVehicleNumber() != null) {
					retresponse.setVehicleNumber(response.getBody().getVehicleNumber());
				} else {
					retresponse.setVehicleNumber("na");
				}

				if (response.getBody().getDriverName() != null) {
					retresponse.setDriverName(response.getBody().getDriverName());
				} else {
					retresponse.setDriverName("na");
				}

				if (response.getBody().getDriverNumber() != null) {
					retresponse.setDriverNumber(response.getBody().getDriverNumber());
				} else {
					retresponse.setDriverNumber("na");
				}

			}

		} catch (Exception ep) {
			log.error("Error while getting the vehicle number" + ep.getLocalizedMessage());

			retresponse.setDriverName("NA");
			retresponse.setDriverNumber("NA");
			retresponse.setVehicleNumber("NA");
			retresponse.setVehicleType("NA");
		}

		return retresponse;
	}
}
