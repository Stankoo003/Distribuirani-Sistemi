package okt2.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) throws Exception {
        MQTTBrokerImpl broker = new MQTTBrokerImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("MQTTBroker", broker);
        System.out.println("MQTT Broker je pokrenut i ceka klijente...");
    }
}