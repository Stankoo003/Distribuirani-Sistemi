package okt2.client;

import okt2.common.MQTTBroker;
import okt2.common.Poruka;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Klijent {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        MQTTBroker broker = (MQTTBroker) registry.lookup("MQTTBroker");

        KlijentMQTTImpl pretplatnik1 = new KlijentMQTTImpl("Ana");
        KlijentMQTTImpl pretplatnik2 = new KlijentMQTTImpl("Marko");
        KlijentMQTTImpl pretplatnik3 = new KlijentMQTTImpl("Jelena");

        System.out.println("=== Pretplata na topike ===");
        broker.subscribe("sport", pretplatnik1);       // Ana -> sport
        broker.subscribe("tehnologija", pretplatnik2); // Marko -> tehnologija
        broker.subscribe("sport", pretplatnik3);       // Jelena -> sport
        broker.subscribe("tehnologija", pretplatnik3); // Jelena -> tehnologija

        System.out.println("\n=== Objavljivanje poruka ===");

        System.out.println("\n-- Publish na 'sport' (ocekuju Ana i Jelena) --");
        broker.publish("sport", new Poruka("Fudbal", "Srbija pobedila 3:0!"));

        System.out.println("\n-- Publish na 'tehnologija' (ocekuju Marko i Jelena) --");
        broker.publish("tehnologija", new Poruka("AI", "Objavljen novi jezicki model."));

        System.out.println("\n-- Publish na 'muzika' (novi topik, nema pretplatnika) --");
        broker.publish("muzika", new Poruka("Koncert", "Nastup u Beogradu veceras."));

        System.out.println("\n-- Ana se pretplacuje na 'muzika' --");
        broker.subscribe("muzika", pretplatnik1);

        System.out.println("\n-- Publish na 'muzika' (ocekuje Ana) --");
        broker.publish("muzika", new Poruka("Festival", "Novi datum objavljen."));

        System.out.println("\n=== Demonstracija zavrsena ===");
    }
}
