import java.rmi.*;

public class ClientAdmin {

    public static void main(String[] args) {
        try {
            FootballScore footballScore = (FootballScore) Naming.lookup("rmi://localhost:4096/FootballScore");

            Match match1 = footballScore.getMatch(1);
            match1.addHomeGoal();

            System.out.println("Added home goal to match 1.");
            System.out.println(match1.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}