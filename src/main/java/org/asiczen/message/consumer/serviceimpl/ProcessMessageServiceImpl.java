package org.asiczen.message.consumer.serviceimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.asiczen.message.consumer.model.ConvertedMessage;
import org.asiczen.message.consumer.model.OriginalMessage;
import org.asiczen.message.consumer.repository.RedisVehicleServiceResponseRespository;
import org.asiczen.message.consumer.response.VehicleServiceResponse;
import org.asiczen.message.consumer.service.ProcessMessageService;
import org.asiczen.message.consumer.service.VehicleInfoPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessMessageServiceImpl implements ProcessMessageService {

	@Value("${BASE.URL}")
	private String BASE_URL;

//	@Autowired
//	SimpMessagingTemplate messageTemplate;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	RedisVehicleServiceResponseRespository redisVInfoRepo;

	@Autowired
	VehicleInfoPublisher redPubService;

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

			log.trace("Converting the date and time to string for displaying on screen.");
			try {

				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				String strDate = dateFormat.format(message.getTimestamp());
				conmessage.setDateTimestamp(strDate);
			} catch (Exception ep) {
				log.error("Error while converting the date");
				log.error(ep.getLocalizedMessage());
			}

			VehicleServiceResponse response = getVehicleInfoFromRedis(message.getImei());

			conmessage.setVehicleNumber(response.getVehicleNumber());
			conmessage.setVehicleType(response.getVehicleType());
			conmessage.setDriverName(response.getDriverName());
			conmessage.setDriverContact(response.getDriverNumber());

			log.info("Final converted message : {}", conmessage.toString());

			redPubService.publish(conmessage);

			// Publish the converted message to Redis topic
			// Publish the converted message to Rabbit MQ

		} catch (Exception ep) {

			log.warn("Error while persisting the message");
			log.warn(ep.getLocalizedMessage());
			log.warn(message.toString());
			log.error("Error while persisting the message. {}", ep.getMessage());
		}

	}

	/*
	 * Check in redis cache if data is already present if not present then get the
	 * data from
	 */
	private VehicleServiceResponse getVehicleInfoFromRedis(String imei) {

		VehicleServiceResponse response = new VehicleServiceResponse("na", "na", "na", "na");

		try {
			VehicleServiceResponse message = redisVInfoRepo.findByImei(imei);

			log.trace("Message from server {} ", message.toString());

			if (message != null && !message.getVehicleNumber().equalsIgnoreCase("NA")) {

				if (message.getVehicleNumber() != null) {
					response.setVehicleNumber(message.getVehicleNumber());
				} else {
					response.setVehicleNumber("na");
				}

				if (message.getVehicleType() != null) {
					response.setVehicleType(message.getVehicleType());
				} else {
					response.setVehicleType("car");
				}

				if (message.getDriverName() != null) {
					response.setDriverName(message.getDriverName());
				} else {
					response.setDriverName("na");
				}

				if (message.getDriverNumber() != null) {
					response.setDriverNumber(message.getDriverNumber());
				} else {
					response.setDriverNumber("na");
				}

				log.trace("Retrieved data from redis");

			} else {
				response = getVehicleInformationFromRestService(imei);
				redisVInfoRepo.update(response, imei);

				log.trace("Retrieved data from webservice");
			}

		} catch (Exception ep) {
			log.error("Cache miss occured " + ep.getLocalizedMessage());
			log.warn("This was a cache miss , so now getting data from service.");
		}

		return response;
	}

	/*
	 * Method is to get the vehicle information from database i.e by calling the web
	 * service defined in application.properties file Finally add the map into Redis
	 */
	private VehicleServiceResponse getVehicleInformationFromRestService(String imei) {

		log.trace("Getting vehicle information by calling the rest web service..");
		VehicleServiceResponse retresponse = new VehicleServiceResponse();

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("imei", imei);

		log.trace("Queury partamter to web service is IMEI number {} ", imei);

		try {
			log.trace("invoking rest template to get the data for IMEI");
			ResponseEntity<VehicleServiceResponse> response = restTemplate.getForEntity(BASE_URL + "?imei=" + imei,
					VehicleServiceResponse.class, queryParams);

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

			// now cache this data

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
