package zadatak5.server;

import zadatak5.common.Eksponat;
import zadatak5.common.KlijentAukcije;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EksponatImpl extends UnicastRemoteObject implements Eksponat {

    private final String id;
    private final String naziv;
    private int cena;
    private KlijentAukcije trenutniLicitant;

    public EksponatImpl(String id, String naziv, int cena) throws RemoteException {
        this.id = id;
        this.naziv = naziv;
        this.cena = cena;
    }

    @Override
    public void prijaviLicitaciju(KlijentAukcije ka) throws RemoteException {
        this.trenutniLicitant = ka;
    }

    @Override
    public KlijentAukcije vratiKlijentaAukcije() throws RemoteException {
        return trenutniLicitant;
    }

    @Override
    public void odustaniOdLicitacije(String klijentAukcijeId) throws RemoteException {
        if (trenutniLicitant != null && trenutniLicitant.getKlijentAukcijeId().equals(klijentAukcijeId)) {
            trenutniLicitant = null;
        }
    }

    @Override
    public String vratiNaziv() throws RemoteException {
        return naziv;
    }

    @Override
    public int vratiCenu() throws RemoteException {
        return cena;
    }

    @Override
    public void povecajCenu(int iznos) throws RemoteException {
        this.cena += iznos;
    }
}