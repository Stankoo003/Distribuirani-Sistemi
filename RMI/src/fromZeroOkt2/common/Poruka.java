package fromZeroOkt2.common;

import java.io.Serializable;

public class Poruka implements Serializable {

    private final String naslov;
    private final String sadrzaj;

    public Poruka(String naslov, String sadrzaj){
        this.naslov = naslov;
        this.sadrzaj = sadrzaj;
    }

    public String getNaslov() {return naslov;}
    public String getSadrzaj() {return sadrzaj;}

    @Override
    public String toString() {return "[" + naslov + "] " + sadrzaj;}
}
