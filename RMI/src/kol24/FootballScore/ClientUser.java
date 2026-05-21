import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class ClientUser {

    private FootballScore footballScore;
    private ResultCallback cb;

    public ClientUser() {
        try {
            footballScore = (FootballScore) Naming.lookup("rmi://localhost:4096/FootballScore");

            System.out.println("=== All matches ===");
            System.out.println(footballScore.getAllMatches());

            Match match2 = footballScore.getMatch(2);
            Stadium stadium2 = match2.getStadium();
            System.out.println("Stadium for match 2: " + stadium2.getName() + ", " + stadium2.getCity());

            Match match1 = footballScore.getMatch(1);
            cb = new ResultCallbackImpl();
            match1.register(cb);
            System.out.println("Subscribed to match 1. Press Enter to exit...");

            System.in.read();

            match1.unregister(cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResultChanged(int matchId) {
        try {
            Match m = footballScore.getMatch(matchId);
            System.out.println("Result changed! " + m.getResult());
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        new ClientUser();
    }

    public class ResultCallbackImpl extends UnicastRemoteObject implements ResultCallback {

        protected ResultCallbackImpl() throws RemoteException {
            super();
        }

        @Override
        public void resultChanged(int matchId) throws RemoteException {
            onResultChanged(matchId);
        }
    }
}