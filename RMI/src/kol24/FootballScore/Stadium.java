import java.io.Serializable;

public class Stadium implements Serializable {

    private String name;
    private String city;

    public Stadium(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }
}