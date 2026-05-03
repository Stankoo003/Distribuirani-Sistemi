package zadatak1.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Pitanje extends Remote {
    String vratiTekst() throws RemoteException;
}
