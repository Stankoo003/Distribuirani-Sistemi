import java.rmi.*;

public interface ChatServer extends Remote {
    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException;
    public void register(String username, ChatCallback cb) throws RemoteException;
    public void unregister(String username) throws RemoteException;
}