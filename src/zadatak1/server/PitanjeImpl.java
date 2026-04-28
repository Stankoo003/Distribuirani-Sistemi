package zadatak1.server;

import zadatak1.common.Pitanje;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PitanjeImpl extends UnicastRemoteObject implements Pitanje {

    private final String tekst;
    private final String a;
    private final String b;
    private final String c;

    public PitanjeImpl(String tekst, String a, String b, String c) throws RemoteException {
        this.tekst = tekst;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String vratiTekst() throws RemoteException {
        return tekst + "\na) " + a + " b) " + b + " c) " + c;
    }
}
