import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;

public class FootballScoreImpl extends UnicastRemoteObject implements FootballScore {

    private HashMap<Integer, Match> matches;

    protected FootballScoreImpl() throws RemoteException {
        super();
        matches = new HashMap<Integer, Match>();

        Match m1 = new MatchImpl(1, "Real Madrid", "Barcelona", new Stadium("Santiago Bernabeu", "Madrid"));
        Match m2 = new MatchImpl(2, "Manchester City", "Liverpool", new Stadium("Etihad Stadium", "Manchester"));

        matches.put(1, m1);
        matches.put(2, m2);
    }

    @Override
    public String getAllMatches() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        for (Match m : matches.values()) {
            sb.append(m.getResult()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Match getMatch(int id) throws RemoteException {
        return matches.get(id);
    }
}
