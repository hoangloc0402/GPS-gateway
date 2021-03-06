package com.hoangloc0402.gps.gateway;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class Gateway {
	public static HashMap<String,JSONObject> hashMap = new HashMap<>();
	public static void main(String[] args) throws Exception{
		final int port = 7777;
		DatagramSocket serverSocket = new DatagramSocket(port);
		byte[] receiveData = new byte[1024];

		Publisher publisher = new Publisher("[Publisher]");
		publisher.connect();
		DataPublishingThread p = new DataPublishingThread(publisher);//thread for publishing data to mqtt
		p.start();
		System.out.println("Gateway in running at port: "+port);

		try {
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String receiveMessage = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
				//System.out.println(sentence);
				new Thread(()->{
					synchronized (hashMap) {
						JSONObject jo = new JSONObject(receiveMessage);
						hashMap.put(jo.getString("id"),jo);
					}
				}).start();

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
