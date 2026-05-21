import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
    HashMap<String, ChatCallback> callbacks;
    
    public ChatServerImpl() throws RemoteException {
        super();
        callbacks = new HashMap<>();
    }

    public void sendMessage(String usernameFrom, String usernameTo, String message) throws RemoteException {
        if (!usernameExists(usernameTo) || !usernameExists(usernameFrom)) {
            System.out.println("Wrong username.");
            return;
        }
        ChatCallback cb = callbacks.get(usernameTo);
        cb.onMessageReceived(usernameFrom, message);
    }

    public synchronized void register(String username, ChatCallback cb) throws RemoteException {
        if (usernameExists(username)) return;
        System.out.println("Adding " + username + " to chat server.");
        callbacks.put(username, cb);
    }

    public synchronized void unregister(String username) throws RemoteException {
        if (!usernameExists(username))  return;
        System.out.println("Removing " + username + " from chat server.");
        callbacks.remove(username);
    }

    private boolean usernameExists(String username) {
        return callbacks.containsKey(username);
    }
}