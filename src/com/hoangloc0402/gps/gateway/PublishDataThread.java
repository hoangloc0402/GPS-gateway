package com.hoangloc0402.gps.gateway;

public class PublishDataThread extends Thread{
    Publisher publisher;
    public PublishDataThread(Publisher publisher){
        this.publisher = publisher;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(200);
                if (publisher.isConnected()) {
                    if(!publisher.PublishQueue.isEmpty()) publisher.publishTextMessage((publisher.getMessage()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
