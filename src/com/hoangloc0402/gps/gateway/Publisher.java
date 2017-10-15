package com.hoangloc0402.gps.gateway;

import java.util.LinkedList;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

public class Publisher implements MqttCallback, IMqttActionListener {
    private static final String MqttUserName = "tedfdjcr";
    private static final String MqttPassword = "yoH3kIKmjikr";
    private static final String MqttServerURL = "tcp://m13.cloudmqtt.com:19122";
    private static final String TOPIC = "AssignmentNetworking";
    private static final String ENCODING = "UTF-8";
    private static final int QUALITY_OF_SERVICE = 2;

    public LinkedList<String> PublishQueue = new LinkedList<>();
    protected String name;
    private String clientId;
    public MqttAsyncClient client;
    private MemoryPersistence memoryPersistence;
    private IMqttToken connectToken;
    private IMqttToken subscribeToken;

    public Publisher(String name) { this.name = name; }

    public String getName() { return name; }

    public void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(MqttUserName);
            options.setPassword(MqttPassword.toCharArray());

            memoryPersistence = new MemoryPersistence();
            clientId = MqttAsyncClient.generateClientId();
            client = new MqttAsyncClient(MqttServerURL, clientId, memoryPersistence);
            client.setCallback(this);
            connectToken = client.connect(options, null, this);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return (client != null) && (client.isConnected());
    }

    public void addMessage(String msg){
        PublishQueue.addLast(msg);
    }

    public String getMessage(){
        return PublishQueue.poll();
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if (asyncActionToken.equals(connectToken)) {
            System.out.println( String.format("%s successfully connected",name));
            try {
                subscribeToken = client.subscribe(TOPIC, QUALITY_OF_SERVICE, null, this);
            }
            catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else if (asyncActionToken.equals(subscribeToken)) {
            System.out.println( String.format("%s subscribed to the %s topic", name, TOPIC));
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        exception.printStackTrace();
    }

    public MessageActionListener publishTextMessage(String messageText) {
        byte[] bytesMessage;
        try {
            bytesMessage = messageText.getBytes(ENCODING);
            MqttMessage message = new MqttMessage(bytesMessage);
            String userContext = "ListeningMessage";
            MessageActionListener actionListener = new MessageActionListener(TOPIC, messageText, userContext);
            client.publish(TOPIC, message, userContext,	actionListener);
            return actionListener;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //Publisher don't need to receive the message they've just publish!
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Delivery for a message has been completed
        // and all acknowledgments have been received
    }
}