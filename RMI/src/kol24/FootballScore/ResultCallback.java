import java.rmi.*;

public interface ResultCallback extends Remote {

    public void resultChanged(int matchId) throws RemoteException;
}