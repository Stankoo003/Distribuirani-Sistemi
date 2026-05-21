import java.rmi.*;

public interface FootballScore extends Remote {

    public String getAllMatches() throws RemoteException;
    public Match getMatch(int id) throws RemoteException;
}
