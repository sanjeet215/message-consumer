package org.asiczen.message.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConvertedMessage {

	private String vehicleNumber;
	private String vehicleType;
	private String imeiNumber;
	private Double longitude;
	private Double latitude;
	private boolean current;
	private String driverName;
	private String driverContact;
	private String dateTimestamp;
}
