package zadatak5.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) throws Exception {
        EAukcijaImpl aukcija = new EAukcijaImpl();
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("EAukcija", aukcija);
        System.out.println("Server aukcije je pokrenut i ceka klijente...");
    }
}