package zadatak5.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EAukcija extends Remote {
    Eksponat vratiEksponat(String idEksponata) throws RemoteException;
}
