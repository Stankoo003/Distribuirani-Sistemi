package fromZeroOkt2.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MQTTBroker extends Remote {
    void createTopic(String topik) throws RemoteException;
    void subscribe(String topik, KlijentMQTT klijentMQTT) throws RemoteException;
    void publish(String topik, Poruka poruka) throws  RemoteException;
}
