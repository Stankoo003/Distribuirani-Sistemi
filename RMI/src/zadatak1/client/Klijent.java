package zadatak1.client;

import zadatak1.common.Kviz;
import zadatak1.common.Pitanje;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Klijent {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        Kviz kviz = (Kviz) registry.lookup("Kviz");

        kviz.pocetak();

        Scanner scanner = new Scanner(System.in);

        for (int i = 1; i <= 3; i++) {
            System.out.println("Pitanje " + i);
            Pitanje pitanje = kviz.vratiPitanje();
            System.out.println(pitanje.vratiTekst());
            System.out.println("Unesite odgovor:");
            String odgovor = scanner.nextLine().trim();
            kviz.odgovori(odgovor);
        }

        System.out.println("Broj Poena:");
        System.out.println(kviz.vratiBrojPoena());

        scanner.close();
    }
}
