package com.hoangloc0402.gps.gateway;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Calendar;

public class Gateway {
	public static void main(String[] args) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];
		byte[] sendData ;

		Publisher publisher = new Publisher("[Publisher]");
		publisher.connect();
		System.out.println("Gateway is running...");
		long preTime = Calendar.getInstance().getTimeInMillis();
		long curTime;
		try {
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				String sentence = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
				System.out.println(sentence);
				publisher.addMessage(sentence);
				curTime = Calendar.getInstance().getTimeInMillis();
				if(curTime - preTime > 10000) {
					preTime = curTime;
					if (publisher.isConnected()) {
						if(!publisher.PublishQueue.isEmpty()) publisher.publishTextMessage((publisher.getMessage()));
					}
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
