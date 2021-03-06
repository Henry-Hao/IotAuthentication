package Test;

import java.io.IOException;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.jose4j.lang.JoseException;

import Client.Client;
import Server.Pushback;
import Util.Utility;


/**
 * 
 * 
 * This test is for the situation that the public key of the device is not found on the server
 * 
 * 1. Run the program to create the correct key pairs
 * 2. Delete the certain public key under Server's directory (keypairs)
 * 3. Run the program
 * 4. Recover the second step
 */

public class Testing4 {

	
	static String username = "admin";
	static String password = "public";
	static MqttTopic topic1;
	static String clientId_1 = "Server";
	static String TOPIC_1 = "Testing_Topic";
	public static void main(String[] args) throws UnknownHostException, MqttException, IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeySpecException, JoseException {
		Client client = new Client("tcp://127.0.0.1:11883", "aaa");
		Client server = new Client("tcp://127.0.0.1:11883", clientId_1);
		
		MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        try {
            server.setCallback(new Pushback(server, clientId_1));
            client.connect(options);
            server.connect(options);
            topic1 = server.getTopic(TOPIC_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        server.subscribe(TOPIC_1);
		
        MqttMessage message = new MqttMessage();
        message.setQos(2);
        message.setPayload(Utility.createJwtEs(clientId_1, "content", "This is the message").getBytes());
        
        topic1.publish(message);        
        client.disconnect();
        server.disconnect();
        client.close();
        server.close();
        

	}


}
