package org.asiczen.message.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceResponse {

	String vehicleNumber;
	String vehicleType;
	String driverName;
	String driverNumber;

}
