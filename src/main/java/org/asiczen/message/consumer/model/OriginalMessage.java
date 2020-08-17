package org.asiczen.message.consumer.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OriginalMessage {

	@JsonProperty("IMEI")
	private String imei;

	@JsonProperty("GPS")
	private int gps;

	@JsonProperty("Lat")
	private double lat;

	@JsonProperty("Lng")
	private double lng;

	@JsonProperty("IsKeyOn")
	private boolean isKeyOn;

	@JsonProperty("Heading")
	private int heading;

	@JsonProperty("Timestamp")
	private Date timestamp;
}