package com.hoangloc0402.gps.gateway;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Gateway {
	public static void main(String[] args) {
		Publisher publisher = new Publisher("[Publisher]");
		publisher.connect();

		int i=0;
		try {
			while (true) {
				try {
					Thread.sleep(2000);
					if (publisher.isConnected()) {
						publisher.addMessage("Fuck you " + i); i++;
						publisher.publishTextMessage((publisher.getMessage()));
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (publisher.isConnected()) {
				try {
					publisher.client.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
