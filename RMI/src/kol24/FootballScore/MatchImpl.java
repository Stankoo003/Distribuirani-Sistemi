import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;

public class MatchImpl extends UnicastRemoteObject implements Match {

    private int id;
    private String homeTeam;
    private String awayTeam;
    private int homeGoals;
    private int awayGoals;
    private Stadium stadium;
    private ArrayList<ResultCallback> callbacks = new ArrayList<ResultCallback>();

    protected MatchImpl(int id, String homeTeam, String awayTeam, Stadium stadium) throws RemoteException {
        super();
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = 0;
        this.awayGoals = 0;
        this.stadium = stadium;
    }

    @Override
    public void addHomeGoal() throws RemoteException {
        homeGoals++;
        callCallbacks();
    }

    @Override
    public void addAwayGoal() throws RemoteException {
        awayGoals++;
        callCallbacks();
    }

    @Override
    public Stadium getStadium() throws RemoteException {
        return stadium;
    }

    @Override
    public String getResult() throws RemoteException {
        return "Match " + id + ": " + homeTeam + " " + homeGoals + " - " + awayGoals + " " + awayTeam;
    }

    @Override
    public synchronized void register(ResultCallback cb) throws RemoteException {
        callbacks.add(cb);
    }

    @Override
    public synchronized void unregister(ResultCallback cb) throws RemoteException {
        callbacks.remove(cb);
    }

    private void callCallbacks() {
        try {
            for (ResultCallback cb : callbacks) {
                cb.resultChanged(id);
            }
        } catch (Exception e) {

        }
    }
}