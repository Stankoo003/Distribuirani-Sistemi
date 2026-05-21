import java.rmi.*;

public interface Match extends Remote {

    public void addHomeGoal() throws RemoteException;
    public void addAwayGoal() throws RemoteException;
    public Stadium getStadium() throws RemoteException;
    public String getResult() throws RemoteException;
    public void register(ResultCallback cb) throws RemoteException;
    public void unregister(ResultCallback cb) throws RemoteException;
}