import java.rmi.*;

public interface ChatCallback extends Remote {
    public void onMessageReceived(String usernameFrom, String message) throws RemoteException;
}
