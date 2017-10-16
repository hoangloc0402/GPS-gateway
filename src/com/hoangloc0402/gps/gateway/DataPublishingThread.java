package com.hoangloc0402.gps.gateway;

import org.json.JSONObject;

public class DataPublishingThread extends Thread{
    Publisher publisher;
    public DataPublishingThread(Publisher publisher){
        this.publisher = publisher;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
                synchronized (Gateway.messageList) {
                    if (publisher.isConnected()) {
                        try {
                            JSONObject jo = new JSONObject();
                            jo.put("list",Gateway.messageList);
                            publisher.publishTextMessage(jo.toString());
                            Gateway.messageList.clear();
                        }
                        catch (Exception e){}
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
