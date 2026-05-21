import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;

public class Server {

    public Server() {
        try {
            LocateRegistry.createRegistry(4096);
            System.out.println("Java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("Java RMI registry already exists.");
        }

        try {
            FootballScoreImpl fs = new FootballScoreImpl();
            Naming.rebind("rmi://localhost:4096/FootballScore", fs);
        } catch (RemoteException e) {
            System.out.println("Failure during RMI object creation: " + e);
        } catch (MalformedURLException e) {
            System.out.println("Failure during Name registration: " + e);
        }
    }

    public static void main(String[] args) {
        new Server();
        System.out.println("Server started.");

        try {
            System.in.read();
        } catch (IOException e) {

        }
    }
}