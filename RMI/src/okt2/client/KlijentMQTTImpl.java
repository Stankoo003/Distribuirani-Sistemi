package okt2.client;

import okt2.common.KlijentMQTT;
import okt2.common.Poruka;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class KlijentMQTTImpl extends UnicastRemoteObject implements KlijentMQTT {

    private final String ime;

    public KlijentMQTTImpl(String ime) throws RemoteException {
        this.ime = ime;
    }

    @Override
    public void primiPoruku(String topik, Poruka poruka) throws RemoteException {
        System.out.println("  [" + ime + "] Poruka na topiku '" + topik + "': " + poruka);
    }
}
