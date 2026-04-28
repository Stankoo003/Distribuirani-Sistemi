package zadatak5.common;

import java.io.Serializable;

public class KlijentAukcije implements Serializable {

    private final String klijentAukcijeId;
    private final String ime;
    private final String prezime;

    public KlijentAukcije(String klijentAukcijeId, String ime, String prezime) {
        this.klijentAukcijeId = klijentAukcijeId;
        this.ime = ime;
        this.prezime = prezime;
    }

    public String getKlijentAukcijeId() { return klijentAukcijeId; }
    public String getIme() { return ime; }
    public String getPrezime() { return prezime; }
}
