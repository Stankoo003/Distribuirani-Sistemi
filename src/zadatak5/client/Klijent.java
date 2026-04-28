package zadatak5.client;

import zadatak5.common.EAukcija;
import zadatak5.common.Eksponat;
import zadatak5.common.KlijentAukcije;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Klijent {

    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        EAukcija aukcija = (EAukcija) registry.lookup("EAukcija");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Dobrodosli na elektronsku aukciju. Za nastavak unesite vase licne podatke:");

        System.out.println("Identifikator:");
        System.out.print("/>");
        String klijentId = scanner.nextLine().trim();

        System.out.println("Ime");
        System.out.print("/>");
        String ime = scanner.nextLine().trim();

        System.out.println("Prezime");
        System.out.print("/>");
        String prezime = scanner.nextLine().trim();

        KlijentAukcije ka = new KlijentAukcije(klijentId, ime, prezime);

        while (true) {
            System.out.println("Unesite identifikator za eksponat od interesa:");
            System.out.print("/>");
            String eksponatId = scanner.nextLine().trim();

            if (eksponatId.isEmpty() || eksponatId.equalsIgnoreCase("q")) break;

            Eksponat eksponat = aukcija.vratiEksponat(eksponatId);
            if (eksponat == null) {
                System.out.println("Eksponat sa tim identifikatorom ne postoji.");
                continue;
            }

            System.out.println("Naziv eksponata je:");
            System.out.println(eksponat.vratiNaziv());
            System.out.println("Cena eksponata je:");
            System.out.println(eksponat.vratiCenu());

            System.out.println("Izaberite opciju:");
            System.out.println("a) Licitacija");
            System.out.println("b) Odustajanje");
            System.out.print("/>");
            String opcija = scanner.nextLine().trim();

            if (opcija.equals("a")) {
                System.out.println("Za koliko uvecavate iznos eksponata ?");
                System.out.print("/>");
                int iznos = Integer.parseInt(scanner.nextLine().trim());
                eksponat.povecajCenu(iznos);
                eksponat.prijaviLicitaciju(ka);
            } else if (opcija.equals("b")) {
                eksponat.odustaniOdLicitacije(ka.getKlijentAukcijeId());
            }
        }

        scanner.close();
    }
}