package okt2.server;

import okt2.common.KlijentMQTT;
import okt2.common.MQTTBroker;
import okt2.common.Poruka;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTTBrokerImpl extends UnicastRemoteObject implements MQTTBroker {

    private final Map<String, List<KlijentMQTT>> topici = new HashMap<>();

    public MQTTBrokerImpl() throws RemoteException {}

    @Override
    public void createTopic(String topik) throws RemoteException {
        if (!topici.containsKey(topik)) {
            topici.put(topik, new ArrayList<>());
            System.out.println("Kreiran topik: " + topik);
        }
    }

    @Override
    public void subscribe(String topik, KlijentMQTT klijent) throws RemoteException {
        createTopic(topik);
        topici.get(topik).add(klijent);
        System.out.println("Novi pretplatnik na topik '" + topik + "'.");
    }

    @Override
    public void publish(String topik, Poruka poruka) throws RemoteException {
        createTopic(topik);
        List<KlijentMQTT> pretplatnici = topici.get(topik);
        System.out.println("Objavljena poruka na topik '" + topik + "': " + poruka
                + " (" + pretplatnici.size() + " pretplatnik/a)");
        for (KlijentMQTT klijent : new ArrayList<>(pretplatnici)) {
            try {
                klijent.primiPoruku(topik, poruka);
            } catch (RemoteException e) {
                System.out.println("Klijent nedostupan, uklanjam sa topika '" + topik + "'.");
                pretplatnici.remove(klijent);
            }
        }
    }
}
