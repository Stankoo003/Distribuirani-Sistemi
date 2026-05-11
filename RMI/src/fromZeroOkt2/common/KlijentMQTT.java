package fromZeroOkt2.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KlijentMQTT extends Remote {
    void primiPoruku(String topik, Poruka poruka) throws RemoteException;

}
