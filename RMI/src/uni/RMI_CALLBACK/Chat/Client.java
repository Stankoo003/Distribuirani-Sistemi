import java.rmi.*;
import java.rmi.server.*;
import java.util.Scanner;

public class Client {
    
    private ChatCallback cb;
    private ChatServer cs;
    private String username;

    public Client(String chatName, String username) { 
        try {
            cs = (ChatServer) Naming.lookup("rmi://localhost:1099/" + chatName);
            cb = new ChatCallbackImpl();
            cs.register(username, cb);
            this.username = username;
        } catch (Exception e) {

        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.println("Enter 's' for sending a new message.\nEnter anything else to exit.");
                String input = scanner.nextLine();
                if (!input.equals("s")) break;

                System.out.println("Enter receiver's username: ");
                String usernameTo = scanner.nextLine();
                System.out.println("Enter message: ");
                String message = scanner.nextLine();

                cs.sendMessage(username, usernameTo, message);
            }

            cs.unregister(username);
        } catch (Exception e) {

        }

        scanner.close();
    }

    private void showMessage(String usernameFrom, String message) {
        System.out.println("New message received.");
        System.out.println(usernameFrom + ": " + message);
    }

    public static void main(String[] args) {
        String chatName = args[0];
        String username = args[1];

        new Client(chatName, username).run();
    }

    public class ChatCallbackImpl extends UnicastRemoteObject implements ChatCallback {

        public ChatCallbackImpl() throws RemoteException {
            super();
        }

        public void onMessageReceived(String usernameFrom, String message) throws RemoteException {
            showMessage(usernameFrom, message);
        }
    }
}
