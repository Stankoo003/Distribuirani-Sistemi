package zadatak1.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) throws Exception {
        KvizImpl kviz = new KvizImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("Kviz", kviz);
        System.out.println("Server je pokrenut i ceka klijente...");
    }
}