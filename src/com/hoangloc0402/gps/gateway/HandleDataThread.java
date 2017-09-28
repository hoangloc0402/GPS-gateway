package com.hoangloc0402.gps.gateway;

import java.net.DatagramPacket;

public class HandleDataThread extends Thread{
    DatagramPacket receivePacket;
    Publisher publisher;
    public HandleDataThread(DatagramPacket receivePacket,Publisher publisher){
        this.receivePacket = receivePacket;
        this.publisher = publisher;
    }

    @Override
    public void run() {
        String sentence = new String(receivePacket.getData(),receivePacket.getOffset(),receivePacket.getLength());
        System.out.println("RECEIVE: "+sentence);
        synchronized (publisher) {
            publisher.addMessage(sentence);
        }
    }
}
