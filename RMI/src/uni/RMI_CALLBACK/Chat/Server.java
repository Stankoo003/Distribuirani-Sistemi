import java.rmi.*;
import java.rmi.registry.*;

public class Server {
    public Server(String chatName) {
        try {
            ChatServer cs = new ChatServerImpl();
            LocateRegistry.createRegistry(1099);
            Naming.rebind(chatName, cs);
            System.in.read();
        }
        catch (Exception e) {

        }
        
    }

    public static void main(String args[]) {
        String chatName = args[0];
        new Server(chatName);
    }
}
