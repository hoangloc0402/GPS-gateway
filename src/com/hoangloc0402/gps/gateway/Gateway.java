package com.hoangloc0402.gps.gateway;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;

public class Gateway {
	public static void main(String[] args) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];

		Publisher publisher = new Publisher("[Publisher]");
		publisher.connect();
		PublishDataThread p = new PublishDataThread(publisher);//thread for publishing data to mqtt
		p.start();
		try {
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				HandleDataThread h = new HandleDataThread(receivePacket,publisher);//thread for handling data
				h.start();
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
