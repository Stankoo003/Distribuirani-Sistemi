package zadatak1.server;

import zadatak1.common.Kviz;
import zadatak1.common.Pitanje;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class KvizImpl extends UnicastRemoteObject implements Kviz {

    private final Pitanje[] pitanja;
    private final String[] tacniOdgovori;
    private int brojPoena;
    private int trenutniIndeks;

    public KvizImpl() throws RemoteException {
        pitanja = new Pitanje[3];
        tacniOdgovori = new String[3];

        pitanja[0] = new PitanjeImpl("1+1= ?", "1", "2", "3");
        tacniOdgovori[0] = "b";

        pitanja[1] = new PitanjeImpl("2*3= ?", "6", "2", "1");
        tacniOdgovori[1] = "a";

        pitanja[2] = new PitanjeImpl("10/2= ?", "1", "2", "5");
        tacniOdgovori[2] = "c";
    }

    @Override
    public void pocetak() throws RemoteException {
        brojPoena = 0;
        trenutniIndeks = 0;
    }

    @Override
    public Pitanje vratiPitanje() throws RemoteException {
        return pitanja[trenutniIndeks++];
    }

    @Override
    public void odgovori(String odg) throws RemoteException {
        if (odg.equals(tacniOdgovori[trenutniIndeks - 1])) {
            brojPoena++;
        }
    }

    @Override
    public int vratiBrojPoena() throws RemoteException {
        return brojPoena;
    }
}
