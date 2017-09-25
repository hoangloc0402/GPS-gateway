package com.hoangloc0402.gps.gateway;

import java.util.LinkedList;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;

public class Publisher implements MqttCallback, IMqttActionListener {
    public static final String MqttUserName = "zvzzpcnw";
    public static final String MqttPassword = "NAcTd5BI5Sfu";
    public static final String MqttServerURL = "tcp://m20.cloudmqtt.com:11297";
    public static final String TOPIC = "AssignmentNetworking";
    public static final String ENCODING = "UTF-8";
    public static final int QUALITY_OF_SERVICE = 2;

    protected LinkedList<String> PublishQueue = new LinkedList<>();
    protected String name;
    protected String clientId;
    protected MqttAsyncClient client;
    protected MemoryPersistence memoryPersistence;
    protected IMqttToken connectToken;
    protected IMqttToken subscribeToken;

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
            // I want to use this instance as the callback
            client.setCallback(this);
            connectToken = client.connect(options, null, this);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
//ahahaha
    public boolean isConnected() {
        return (client != null) && (client.isConnected());
    }

    public void addMessage(String msg){
        PublishQueue.addLast(msg);
    }

    public String getMessage(){
        if (PublishQueue.isEmpty()) return "";
        else return PublishQueue.poll();
    }

    @Override
    public void connectionLost(Throwable cause) {
        // The MQTT client lost the connection
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
        // The method will run if an operation failed
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