package zadatak5.server;

import zadatak5.common.EAukcija;
import zadatak5.common.Eksponat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class EAukcijaImpl extends UnicastRemoteObject implements EAukcija {

    private final Map<String, Eksponat> eksponati;

    public EAukcijaImpl() throws RemoteException {
        eksponati = new HashMap<>();
        eksponati.put("EKS_991", new EksponatImpl("EKS_991", "Stara vaza", 99200));
        eksponati.put("EKS_992", new EksponatImpl("EKS_992", "Rimski novac", 5500));
        eksponati.put("EKS_993", new EksponatImpl("EKS_993", "Barokna slika", 32000));
        eksponati.put("EKS_994", new EksponatImpl("EKS_994", "Bronzana figurica", 8750));
        eksponati.put("EKS_997", new EksponatImpl("EKS_997", "Zlatni prsten", 1200));
    }

    @Override
    public Eksponat vratiEksponat(String idEksponata) throws RemoteException {
        return eksponati.get(idEksponata);
    }
}