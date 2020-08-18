package org.asiczen.message.consumer.repository;

import java.util.List;

import org.asiczen.message.consumer.response.VehicleServiceResponse;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisVehicleServiceResponseRespository {

	private HashOperations<String, String, VehicleServiceResponse> hashOperations;

	private RedisTemplate<String, ?> redisTemplate;

	public RedisVehicleServiceResponseRespository(RedisTemplate<String, ?> redisTemplate) {
		this.redisTemplate = redisTemplate;
		this.hashOperations = this.redisTemplate.opsForHash();
	}

	public void save(VehicleServiceResponse message, String imei) {
		hashOperations.put("VINFO", imei, message);
	}

	public List<VehicleServiceResponse> frinAll() {
		return hashOperations.values("VINFO");
	}

	public VehicleServiceResponse findByImei(String imei) {
		return hashOperations.get("VINFO", imei);
	}

	public void update(VehicleServiceResponse message, String imei) {
		save(message, imei);
	}

	public void delete(String imei) {
		hashOperations.delete("VINFO", imei);
	}
}
